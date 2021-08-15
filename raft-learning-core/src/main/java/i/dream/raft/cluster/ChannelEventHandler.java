package i.dream.raft.cluster;

import i.dream.ex.PackageParseException;
import i.dream.raft.cluster.message.PayLoad;
import i.dream.raft.cluster.message.PayloadMeta;
import i.dream.raft.cluster.message.handler.AbstractPacketHandler;
import i.dream.raft.cluster.message.handler.HelloPacketHandler;
import i.dream.raft.cluster.message.handler.PacketCallback;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static i.dream.raft.cluster.message.MessageType.CLUSTERMSG_TYPE_TEST;

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
    private final short HEAD_LENGTH = 4;

    private ByteBuffer metaBuffer = ByteBuffer.allocate(HEAD_LENGTH);

    private ByteBuffer payloadBuffer = metaBuffer;

    public static LinkedBlockingQueue<SelectionKey> recvQueue = new LinkedBlockingQueue(4096);

    public Map<SocketChannel, BlockingQueue<PayloadMeta>> sendQueueMap;

    public static ProcessExecutor eventProcessExecutor = new ProcessExecutor();

    private static Map<Integer, PacketCallback> packetCBMap = new HashMap<>(16);

    static {
        packetCBMap.put(CLUSTERMSG_TYPE_TEST.getCode(), (rawPackage, channel) -> {
            PayLoad.HelloPayLoad helloPayLoad = new PayLoad.HelloPayLoad();
            helloPayLoad.read(rawPackage);
            eventProcessExecutor.doRun(new HelloPacketHandler(channel, helloPayLoad));
        });
    }

    public PayloadMeta processPackage(ByteBuffer rawPackage, SocketChannel channel) {

        PayloadMeta packetHeader = PayloadMeta.parse(rawPackage);
        if (PayLoad.MAGIC != packetHeader.getMagic()) {
            log.error("invalid package format, magic:" + rawPackage.getInt());
            throw new PackageParseException("magic is invalid");
        }

        PayloadMeta payloadMeta = null;

        try {
            packetCBMap.get(packetHeader.getType()).handle(rawPackage, channel);
        } catch (Exception e) {
            log.error("process package error", e);
        }

        log.info("type: {} ", packetHeader.getType());
        log.info("flag: {} ", packetHeader.getFlag());
        log.info("digest: {} ", packetHeader.getDigest());

        return payloadMeta;
    }

    @Override
    public void run() {
        while (true) {
            SelectionKey event = null;
            try {
                event = recvQueue.take();
            } catch (InterruptedException e) {
                log.error("recv error", e);
            }

            if (null != event) {
                // read channel
                if (event.isReadable()) {
                    SocketChannel peer = (SocketChannel) event.channel();
                    // to avoid sticky package, so read the length of package first
                    try {
                        int packetSize = peer.read(payloadBuffer);
                        if (packetSize < 0) {
                            log.error("read payload length, unable to read additional data, client:{}", peer.getRemoteAddress());
                            peer.socket().close();
                            peer.close();
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
                            payloadSize = payloadBuffer.getInt();
                            // TODO: 2021/8/10 size check
                        } catch (NumberFormatException e) {
                            log.error("invalid package format", e);
                        }

                        payloadBuffer = ByteBuffer.allocate(payloadSize);
                        payloadBuffer.putInt(payloadSize);

                        try {
                            int c = peer.read(payloadBuffer);
                            if (c < 0) {
                                log.error("read payload, unable to read additional data");
                            }
                        } catch (IOException e) {
                            log.error("read payload ({}) error,", payloadSize,  e);
                        }

                        // read finished ?
                        if (0 == payloadBuffer.remaining()) {
                            payloadBuffer.rewind();
                            // parse package
                            try {
                                processPackage(payloadBuffer, peer);
                            } catch (Exception e) {
                                log.error("parse package error", e);
                            } finally {
                                // reset payload buffer for gc & read next package
                                payloadBuffer = metaBuffer;
                                metaBuffer.rewind();
                            }
                        }
                    }
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
