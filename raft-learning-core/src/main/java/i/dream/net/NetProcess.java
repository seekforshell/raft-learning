package i.dream.net;

import i.dream.RaftServer;
import i.dream.raft.cluster.ChannelEventHandler;
import i.dream.util.RaftConf;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 核心网络收发包处理流程，包括selector模型、事件监听及报文读写
 * @author: yujingzhi
 * Version: 1.0
 */
@Slf4j
public class NetProcess {
    private static Executor selectorExcutor = null;
    private static Executor executorHandler = null;
    private static Selector selector = null;

    public void init() throws IOException {
        selector = Selector.open();
        // 单线程轮询消息事件
        selectorExcutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1));
        executorHandler = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        selectorExcutor.execute(new SelectorProxyTask());
        executorHandler.execute(new ChannelEventHandler());
    }

    public static Selector getSelector() {
        assert null != selector;
        return selector;
    }

    boolean isClusterChannel(ServerSocketChannel channel) {
        if (RaftConf.getClusterServerPort() == channel.socket().getLocalPort()) {
            return true;
        }

        return false;
    }

    class SelectorProxyTask implements Runnable {
        public void run() {

            synchronized (RaftServer.serverStartLock) {
                try {
                    RaftServer.serverStartLock.wait();
                } catch (InterruptedException e) {
                    log.error("selector error:system error");
                    System.exit(1);
                }
            }

            try {
                while (selector.isOpen()) {
                    int cnt = selector.select(100L);
                    if (cnt > 0) {
                        Set<SelectionKey> keys = selector.selectedKeys();
                        for (SelectionKey key : keys) {
                            if (key.isAcceptable()) {
                                ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                                String clientInfo = "";
                                SocketChannel acceptChannel = channel.accept();
                                acceptChannel.configureBlocking(false);
                                clientInfo = acceptChannel.getRemoteAddress().toString();

                                acceptChannel.register(selector, SelectionKey.OP_READ);

                                log.info(String.format("client:%s is accepted!", clientInfo));
                            } else {
                                ChannelEventHandler.recvQueue.put(key);
                            }
                        }

                        keys.clear();
                    }

                    Thread.sleep(100);
                }
            } catch (IOException | InterruptedException e) {
                log.error("select error:", e);
            }
        }
    }
}
