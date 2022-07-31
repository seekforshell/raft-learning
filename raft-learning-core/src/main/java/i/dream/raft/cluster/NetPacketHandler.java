package i.dream.raft.cluster;

import i.dream.net.PacketReceiver;
import i.dream.raft.cluster.message.PayLoad;
import i.dream.raft.cluster.message.PayloadMeta;
import i.dream.raft.cluster.message.handler.HelloPacketHandler;
import i.dream.raft.cluster.message.handler.PacketCallback;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static i.dream.raft.cluster.message.MessageType.CLUSTERMSG_TYPE_TEST;

/**
 * 对报文进行读写
 *
 * @author: yujingzhi
 * Version: 1.0
 */
@Slf4j
public class NetPacketHandler implements Runnable {
    private final String MAGIC_V ="0xffffffff";
    /**
     * 负载元数据信息为8字节，包括4字节的魔术字和4字节的报文长度
     */
    private final short HEAD_LENGTH = 4;

    private ByteBuffer metaBuffer = ByteBuffer.allocate(HEAD_LENGTH);

    private ByteBuffer payloadBuffer = metaBuffer;

    public static LinkedBlockingQueue<PacketReceiver> recvQueue = new LinkedBlockingQueue(4096);

    public static Map<SocketChannel, BlockingQueue<PayloadMeta>> sendQueueMap = new ConcurrentHashMap<>();

    public static ProcessExecutor eventProcessExecutor = new ProcessExecutor();

    private static Map<Integer, PacketCallback> packetCBMap = new HashMap<>(16);

    static {
        packetCBMap.put(CLUSTERMSG_TYPE_TEST.getCode(), (payload, channel) -> {
            PayLoad.HelloPayLoad helloPayLoad = (PayLoad.HelloPayLoad) payload;
            eventProcessExecutor.doRun(new HelloPacketHandler(channel, helloPayLoad));
        });
    }

    public void processPackage(PayloadMeta packet, SocketChannel channel) {

        PayloadMeta payloadMeta = null;

        try {
            packetCBMap.get(packet.getType()).handle(packet, channel);
        } catch (Exception e) {
            log.error("process package error", e);
        }
    }

    @Override
    public void run() {
        while (true) {
            PacketReceiver packet = null;
            try {
                packet = recvQueue.take();
            } catch (InterruptedException e) {
                log.error("recv error", e);
            }

            try {
                processPackage(packet.getPayload(), packet.getChannel());
            } catch (Exception e) {
                log.error("parse package error", e);
            }
        }
    }
}
