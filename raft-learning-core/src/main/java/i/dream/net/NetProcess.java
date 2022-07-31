package i.dream.net;

import i.dream.ex.PackageParseException;
import i.dream.raft.cluster.NetPacketHandler;
import i.dream.raft.cluster.message.PayLoad;
import i.dream.raft.cluster.message.PayloadMeta;
import i.dream.raft.cluster.message.handler.AbstractPacketHandler;
import i.dream.util.RaftConf;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 核心网络收发包处理流程，包括selector模型、事件监听及报文读写
 * @author: yujingzhi
 * Version: 1.0
 */
@Slf4j
public class NetProcess {

    /**
     * 负载元数据信息为8字节，包括4字节的魔术字和4字节的报文长度
     */
    private final short HEAD_LENGTH = 4;

    private static Executor selectorExcutor = null;
    private static Executor executorHandler = null;
    private static Selector selector = null;

    public void init() throws IOException {
        selector = Selector.open();
        // 单线程轮询消息事件
        selectorExcutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1));
        executorHandler = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        selectorExcutor.execute(new SelectorProxyTask());
        executorHandler.execute(new NetPacketHandler());
    }

    /**
     * register
     * @param channel
     * @param opt
     * @throws ClosedChannelException
     */
    public static void registerChannel(SocketChannel channel, int opt) throws ClosedChannelException {
        channel.register(selector, opt);
    }

    public Selector getSelector() {
        assert null != selector;
        return selector;
    }

    boolean isClusterChannel(ServerSocketChannel channel) {
        if (RaftConf.getClusterServerPort() == channel.socket().getLocalPort()) {
            return true;
        }

        return false;
    }

    public void completedReceive(ByteBuffer rawPackage, SocketChannel channel) {
        PayloadMeta packetHeader = PayloadMeta.parse(rawPackage);
        if (PayLoad.MAGIC != packetHeader.getMagic()) {
            log.error("invalid package format, magic:" + rawPackage.getInt());
            throw new PackageParseException("magic is invalid");
        }

        NetPacketHandler.recvQueue.add(new PacketReceiver(packetHeader, channel));
    }

    class SelectorProxyTask implements Runnable {

        private void receive(SelectionKey event) {
            ByteBuffer metaBuffer = ByteBuffer.allocate(HEAD_LENGTH);
            ByteBuffer payloadBuffer = metaBuffer;

            SocketChannel peer = (SocketChannel) event.channel();
            // to avoid sticky package, so read the length of package first
            try {
                int packetSize = peer.read(payloadBuffer);
                if (packetSize < 0) {
                    log.error("read payload length, unable to read additional data, client:{}", peer.getRemoteAddress());
                    peer.socket().close();
                    peer.close();
                    return;
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
                        completedReceive(payloadBuffer, peer);
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
        public void run() {
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

                                SelectionKey event = key;

                                // read channel
                                if (key.isReadable()) {
                                    receive(event);
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
