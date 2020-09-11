package i.dream.net;

import i.dream.Server;
import i.dream.raft.cluster.MessageDispatcher;
import i.dream.util.FileUtil;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
@Slf4j
public class SelectorProxy {
    private static Executor selectorExcutor = null;
    private static Executor executorHandler = null;
    private static Selector selector = null;

    public void init() throws IOException {
        selector = Selector.open();
        // 单线程轮询消息事件
        selectorExcutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1));
        executorHandler = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        selectorExcutor.execute(new SelectorProxyTask());
        executorHandler.execute(new MessageDispatcher());
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
                    int cnt = selector.select(100L);
                    if (cnt > 0) {
                        Set<SelectionKey> keys = selector.selectedKeys();
                        Iterator<SelectionKey> it = keys.iterator();
                        SelectionKey key = null;
                        ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                        for (; it.hasNext(); key = it.next()) {
                            if (key.isAcceptable()) {
                                String clientInfo = "";

                                SocketChannel acceptChannel = channel.accept();
                                clientInfo = acceptChannel.getRemoteAddress().toString();
                                if (isClusterChannel(channel)) {
                                }

                                acceptChannel.register(selector, SelectionKey.OP_READ);

                                log.info(String.format("client:%s is accepted!", clientInfo));
                            } else if (key.isReadable()) {
                                MessageDispatcher.eventQueue.put(key);
                            } else if (key.isWritable()) {
                                SocketChannel clientChannel = (SocketChannel)key.channel();
                            }

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
