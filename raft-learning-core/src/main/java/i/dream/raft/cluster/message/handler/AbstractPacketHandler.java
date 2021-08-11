package i.dream.raft.cluster.message.handler;

import i.dream.raft.cluster.message.PayloadMeta;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class AbstractPacketHandler implements PacketHandler {

    private SocketChannel channel;

    private PayloadMeta key;

    private ByteBuffer toSentBuffer;

    AbstractPacketHandler(SocketChannel channel, PayloadMeta key) {
        this.channel = channel;
        this.key = key;
    }

    public void run() {

    }

    public ByteBuffer getToSentBuffer() {
        return toSentBuffer;
    }
}
