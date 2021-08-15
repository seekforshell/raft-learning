package i.dream.raft.cluster;

import i.dream.raft.cluster.message.handler.PacketHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程定时处理事件
 * @author: yujingzhi
 * Version: 1.0
 */
public class ProcessExecutor {

    private static ExecutorService executor;

    private static AtomicInteger idx = new AtomicInteger(1);

    static {
        final int core = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(core / 2 + 1, core, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()
            , new ThreadFactory() {
            @Override public Thread newThread(Runnable r) {
                return new Thread(r, "raft-event-" + idx.getAndIncrement());
            }
        });
    }

    public void doRun(PacketHandler packetHandler) {
        if (null != packetHandler) {
            executor.submit(packetHandler);
        }
    }
}
