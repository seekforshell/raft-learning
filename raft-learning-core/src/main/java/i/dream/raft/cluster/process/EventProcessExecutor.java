package i.dream.raft.cluster.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 多线程定时处理事件
 * @author: yujingzhi
 * Version: 1.0
 */
public class EventProcessExecutor {

    private static ExecutorService executor;

    static {
        final int core = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(core / 2 + 1, core, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public void doRun(Process process) {
        if (null == process) {
            return;
        }

        executor.submit(process);
    }
}
