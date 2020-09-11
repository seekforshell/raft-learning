package i.dream.raft.cluster;

import i.dream.raft.cluster.message.ClusterMessageType;
import i.dream.raft.cluster.message.HeartBeatMessage;
import i.dream.raft.cluster.process.EventProcessExecutor;
import i.dream.raft.cluster.process.GossipProcessImpl;
import i.dream.raft.cluster.process.Process;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
    private final String MAGIC_V ="0xffffffff";
    /**
     * 负载元数据信息为8字节，包括4字节的魔术字和4字节的报文长度
     */
    private final short HEAD_LENGTH = 2;

    private ByteBuffer metaBuffer = ByteBuffer.allocate(HEAD_LENGTH);

    private ByteBuffer payLoadBuffer = metaBuffer;

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

                    // to avoid sticky package, so read the length of package first
                    try {
                        int c = channel.read(payLoadBuffer);
                        if (c < 0) {
                            log.error("read payload length, unable to read additional data");
                        }
                    } catch (IOException e) {
                        log.error("read error,", e);
                    }

                    if (0 == payLoadBuffer.remaining()) {
                        payLoadBuffer.flip();
                        int lenOfPayLoad = Integer.parseInt(StandardCharsets.UTF_8.decode(payLoadBuffer).toString());
                        payLoadBuffer = ByteBuffer.allocate(lenOfPayLoad);

                        try {
                            int c = channel.read(payLoadBuffer);
                            if (c < 0) {
                                log.error("read payload, unable to read additional data");
                            }
                        } catch (IOException e) {
                            log.error("read error,", e);
                        }

                        if (0 == payLoadBuffer.remaining()) {
                            payLoadBuffer.flip();

                            Charset charset = StandardCharsets.UTF_8;
                            CharBuffer charBuffer = charset.decode(payLoadBuffer);
                            log.info("receive message is :"+ charBuffer.toString());
                            // parse package
                            HeartBeatMessage message = parsePackage(payLoadBuffer.array());
                            if (null != message) {
                                final int msgType = message.getMsgType();
                                if (ClusterMessageType.CLUSTERMSG_TYPE_PING.getCode() == msgType) {
                                    process = new GossipProcessImpl();
                                }
                            }

                            payLoadBuffer = metaBuffer;
                        }
                    }

                    eventProcessExecutor.doRun(process);
                } else if (event.isWritable()) {

                }
            }
        }
    }
}
