package i.dream.raft.cluster;

import i.dream.ex.PackageParseException;
import i.dream.raft.cluster.message.MessageType;
import i.dream.raft.cluster.message.HeartBeatMessage;
import i.dream.raft.cluster.message.PayLoad;
import i.dream.raft.cluster.message.PayloadMeta;
import i.dream.raft.cluster.process.AbstractProcess;
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

    public static LinkedBlockingQueue<SelectionKey> eventQueue = new LinkedBlockingQueue(4096);

    public static EventProcessExecutor eventProcessExecutor = new EventProcessExecutor();

    public PayloadMeta parsePackage(ByteBuffer rawPackage) {
        if (PayLoad.MAGIC != rawPackage.getInt()) {
            log.error("invalid package format, magic:" + rawPackage.getInt());
            throw new PackageParseException("magic is invalid");
        }

        short type = rawPackage.getShort();

        return null;
    }

    @Override
    public void run() {
        while (true) {
            SelectionKey event = eventQueue.poll();
            Process process = null;
            if (null != event) {
                SocketChannel channel = (SocketChannel) event.channel();
                if (event.isReadable()) {
                    // to avoid sticky package, so read the length of package first
                    try {
                        int c = channel.read(payLoadBuffer);
                        if (c < 0) {
                            log.error("read payload length, unable to read additional data");
                            channel.socket().close();
                            channel.close();
                            continue;
                        }
                    } catch (IOException e) {
                        log.error("io error,", e);
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
                            CharBuffer decodedBuff = charset.decode(payLoadBuffer);
                            log.info("receive message is :"+ decodedBuff.toString());

                            // parse package
                            PayloadMeta message = null;
                            try {
                                message = parsePackage(payLoadBuffer);

                                if (null != message) {
                                    final int msgType = message.getType();
                                    if (MessageType.CLUSTERMSG_TYPE_PING.getCode() == msgType) {
                                        process = new GossipProcessImpl(channel, event);
                                        eventProcessExecutor.doRun(process);
                                        // 通过attach的方式来进行写回调
                                        event.attach(process);
                                    }
                                }

                                try {
                                    ByteBuffer buffer = ByteBuffer.wrap("hello baby".getBytes());
                                    int c = channel.write(buffer);
                                    if (c < 0) {
                                        log.error("write error, the result:" + c);
                                    }
                                } catch (IOException e) {
                                    log.error("write error", e);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                payLoadBuffer = metaBuffer;
                                metaBuffer.flip();
                            }
                        }
                    }

                    eventProcessExecutor.doRun(process);
                }

                if (event.isWritable()) {
                    AbstractProcess writeCallback = (AbstractProcess) event.attachment();
                    ByteBuffer outGoingBuffer = writeCallback.getToSentBuffer();

                    try {
                        int c = channel.write(outGoingBuffer);
                    } catch (IOException e) {
                        log.error("write error", e);
                    }

                }
            }
        }
    }
}
