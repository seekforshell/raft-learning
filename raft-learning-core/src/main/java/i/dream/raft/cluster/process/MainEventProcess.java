package i.dream.raft.cluster.process;

import i.dream.raft.cluster.message.ClusterMessageType;
import i.dream.raft.cluster.message.MessageHeader;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static i.dream.Server.serverStartLock;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
@Slf4j
public class MainEventProcess {
    // 定时处理事件：每秒调用一次
    private final short HZ = 1;
    public static BlockingQueue<MessageHeader> messageQueue = new LinkedBlockingQueue(4096);
    public static EventProcessExecutor eventProcessExecutor = new EventProcessExecutor();

    public void run() {
        ScheduledExecutorService scheduleService = new ScheduledThreadPoolExecutor(1);
        scheduleService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    serverStartLock.wait();
                } catch (InterruptedException e) {
                    log.error("event process is interrupted. \r\nsystem error.");
                    System.exit(1);
                }

                while (true) {
                    try {
                        MessageHeader message = messageQueue.poll(1000, TimeUnit.SECONDS);
                        if (null != message && message.getMsgType() == ClusterMessageType.CLUSTERMSG_TYPE_PING.getCode()) {
                            GossipProcess gossipProcess = new GossipProcessImpl();
                            eventProcessExecutor.doRun(gossipProcess);
                        }
                    } catch (InterruptedException e) {
                        log.error("interrupt ", e);
                    }
                }
            }
        }, 0, HZ, TimeUnit.SECONDS);
    }
}
