package i.dream.net;

import i.dream.Server;
import i.dream.raft.cluster.NioEventGroupHandler;
import i.dream.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
@Slf4j
public class SelectorProxy {
    private static Executor executor = null;
    private static Selector selector = null;

    public void init() throws IOException {
        selector = Selector.open();
        // 单线程处理集群报文
        executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        executor.execute(new SelectorProxyTask());
    }

    public static Selector getSelector() {
        assert null != selector;
        return selector;
    }

    boolean isClusterChannel(ServerSocketChannel channel) {
        if (FileUtil.getClusterServerPort() == channel.socket().getLocalPort()) {
            return true;
        }

        return false;
    }

    class SelectorProxyTask implements Runnable {
        public void run() {
            try {
                Server.serverStartLock.wait();
            } catch (InterruptedException e) {
                log.error("selector error:system error");
                System.exit(1);
            }

            try {
                while (selector.isOpen()) {
                    int cnt = selector.select(1000L);
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();
                    SelectionKey key = null;
                    for (; it.hasNext(); key = it.next()) {
                        if (key.isAcceptable()) {
                            SelectableChannel channel = key.channel();
                            if (channel instanceof ServerSocketChannel) {
                                SocketChannel acceptChannel = ((ServerSocketChannel) channel).accept();
                                if (isClusterChannel((ServerSocketChannel) channel)) {
                                }

                                // accept new
                                acceptChannel.register(selector, SelectionKey.OP_READ);
                            }
                        } else if (key.isReadable()) {
                            NioEventGroupHandler.eventQueue.put(key);
                        }
                    }

                    Thread.sleep(100);
                }
            } catch (IOException | InterruptedException e) {
                log.error("select error:", e);
            }
        }
    }
}
