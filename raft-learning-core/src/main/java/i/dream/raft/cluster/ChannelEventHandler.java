package i.dream.raft.cluster;

import i.dream.ex.PackageParseException;
import i.dream.raft.cluster.message.MessageType;
import i.dream.raft.cluster.message.PayLoad;
import i.dream.raft.cluster.message.PayloadMeta;
import i.dream.raft.cluster.message.handler.AbstractPacketHandler;
import i.dream.raft.cluster.message.handler.DemoPacketHandler;
import i.dream.raft.cluster.message.handler.GossipPacketHandlerImpl;
import i.dream.raft.cluster.message.handler.PacketHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 对报文进行读写
 *
 * @author: yujingzhi
 * Version: 1.0
 */
@Slf4j
public class ChannelEventHandler implements Runnable {
    private final String MAGIC_V ="0xffffffff";
    /**
     * 负载元数据信息为8字节，包括4字节的魔术字和4字节的报文长度
     */
    private final short HEAD_LENGTH = 2;

    private ByteBuffer metaBuffer = ByteBuffer.allocate(HEAD_LENGTH);

    private ByteBuffer payloadBuffer = metaBuffer;

    public static LinkedBlockingQueue<SelectionKey> recvQueue = new LinkedBlockingQueue(4096);

    public Map<SocketChannel, BlockingQueue<PayloadMeta>> sendQueueMap;

    public static ProcessExecutor eventProcessExecutor = new ProcessExecutor();

    public PayloadMeta parsePackage(ByteBuffer rawPackage) {
        if (PayLoad.MAGIC != rawPackage.getInt()) {
            log.error("invalid package format, magic:" + rawPackage.getInt());
            throw new PackageParseException("magic is invalid");
        }

        Short packageType = rawPackage.getShort();

        rawPackage.rewind();

        PayloadMeta payloadMeta = null;

        ObjectInputStream payloadStream;
        try {
            payloadStream = new ObjectInputStream(new ByteArrayInputStream(rawPackage.array()));
            if (packageType.equals(1)) {
                payloadMeta = (PayLoad.RequestVotePayLoad)payloadStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        log.info("type: {} ", rawPackage.getShort());
        log.info("flag: {} ", rawPackage.getInt());
        log.info("digest: {} ", rawPackage.getLong());

        return payloadMeta;
    }

    @Override
    public void run() {
        while (true) {
            SelectionKey event = recvQueue.poll();
            PacketHandler packetHandler = null;
            if (null != event) {
                // read channel
                if (event.isReadable()) {
                    SocketChannel channel = (SocketChannel) event.channel();
                    // to avoid sticky package, so read the length of package first
                    try {
                        int packetSize = channel.read(payloadBuffer);
                        if (packetSize < 0) {
                            log.error("read payload length, unable to read additional data");
                            channel.socket().close();
                            channel.close();
                            continue;
                        }
                    } catch (IOException e) {
                        log.error("io error,", e);
                    }

                    // buffer is full filled ?
                    if (0 == payloadBuffer.remaining()) {
                        // reset position of buffer to read packet
                        payloadBuffer.rewind();
                        int payloadSize = 0;
                        try {
                            payloadSize = Integer.parseInt(StandardCharsets.UTF_8.decode(payloadBuffer).toString());
                            // TODO: 2021/8/10 size check
                        } catch (NumberFormatException e) {
                            log.error("invalid package format", e);
                        }

                        payloadBuffer = ByteBuffer.allocate(payloadSize);

                        try {
                            int c = channel.read(payloadBuffer);
                            if (c < 0) {
                                log.error("read payload, unable to read additional data");
                            }
                        } catch (IOException e) {
                            log.error("read payload ({}) error,", payloadSize,  e);
                        }

                        // read finished ?
                        if (0 == payloadBuffer.remaining()) {
                            payloadBuffer.rewind();

                            CharBuffer decodedBuff = StandardCharsets.UTF_8.decode(payloadBuffer);
                            log.info("receive message is :"+ decodedBuff.toString());

                            // parse package
                            PayloadMeta payloadBody = null;
                            try {
                                payloadBody = parsePackage(payloadBuffer);

                                if (null != payloadBody) {
                                    final int msgType = payloadBody.getType();
                                    if (MessageType.CLUSTERMSG_TYPE_TEST.getCode() == msgType) {
                                        packetHandler = new DemoPacketHandler(channel, payloadBody);
                                        packetHandler.run();
                                    }
                                }
                            } catch (Exception e) {
                                log.error("parse package error", e);
                            } finally {
                                // reset payload buffer for gc & read next package
                                payloadBuffer = metaBuffer;
                                metaBuffer.rewind();
                            }
                        }
                    }

                    eventProcessExecutor.doRun(packetHandler);
                }

                // write channel
                if (event.isWritable()) {
                    SocketChannel channel = (SocketChannel) event.channel();
                    AbstractPacketHandler writeCallback = (AbstractPacketHandler) event.attachment();
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
