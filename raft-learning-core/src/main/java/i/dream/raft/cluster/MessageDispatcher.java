package i.dream.raft.cluster;

import i.dream.raft.cluster.message.ClusterMessageType;
import i.dream.raft.cluster.message.HeartBeatMessage;
import i.dream.raft.cluster.process.EventProcessExecutor;
import i.dream.raft.cluster.process.GossipProcessImpl;
import i.dream.raft.cluster.process.Process;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理报文并分发消息到相应的处理器执行
 *
 * @author: yujingzhi
 * Version: 1.0
 */
@Slf4j
public class MessageDispatcher implements Runnable{
    private final String MAGIC_V ="0x0902";

    public static LinkedBlockingQueue<SelectionKey> eventQueue = new LinkedBlockingQueue(1024);

    public static EventProcessExecutor eventProcessExecutor = new EventProcessExecutor();

    public HeartBeatMessage parsePackage(byte[] rawPackage) {

        return null;
    }

    @Override
    public void run() {
        while (true) {
            SelectionKey event = eventQueue.poll();
            Process process = null;
            if (null != event) {
                if (event.isReadable()) {
                    SocketChannel channel = (SocketChannel) event.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
                    try {
                        int c = channel.read(byteBuffer);
                    } catch (IOException e) {
                        log.error("read error,", e);
                    }

                    byte[] msgBuff = byteBuffer.array();
                    log.info("receive message is :"+msgBuff.toString());
                    // parse package
                    HeartBeatMessage message = parsePackage(msgBuff);
                    if (null != message) {
                        final int msgType = message.getMsgType();
                        if (ClusterMessageType.CLUSTERMSG_TYPE_PING.getCode() == msgType) {
                            process = new GossipProcessImpl();
                        }
                    }

                    eventProcessExecutor.doRun(process);
                } else if (event.isWritable()) {

                }
            }
        }
    }
}
