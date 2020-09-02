package i.dream.raft.cluster;

import i.dream.ex.ClusterException;
import i.dream.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 * Create Date Time: 2020-09-01 10:24.
 */
public class ClusterServer {
    private volatile boolean IS_RUNNING = false;
    private Selector selector = null;
    private ServerSocketChannel serverSocketChannel = null;
    private Executor executor = null;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    public void init () {

        SocketAddress socketAddress = new InetSocketAddress(FileUtil.getClusterServerPort());
        executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(socketAddress);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            logger.error("open selector error:", e);
            throw new ClusterException(e.getMessage());
        } catch (IllegalArgumentException ie) {
            logger.error("illegal argument error:", ie);
            throw new ClusterException(ie.getMessage());
        } catch (Exception e) {
            logger.error("unknown error:", e);
            throw new ClusterException(e.getMessage());
        }


        executor.execute(new SelectorProxy());

    }

    class SelectorProxy implements Runnable {

        public SelectorProxy() {
        }

        public void run() {
            try {
                while (selector.isOpen() && IS_RUNNING) {
                    int cnt = selector.select(1000L);
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();
                    SelectionKey key = null;
                    for (; it.hasNext(); key = it.next()) {
                        if (key.isAcceptable()) {
                            SelectableChannel channel = key.channel();
                            if (channel instanceof ServerSocketChannel) {
                                SocketChannel acceptChannel = ((ServerSocketChannel) channel).accept();
                                acceptChannel.register(selector, SelectionKey.OP_READ);
                            }
                        } else if (key.isReadable()) {
                            NioEventGroupHandler.eventQueue.put(key);
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                logger.error("select error:", e);
            }
        }
    }
}
