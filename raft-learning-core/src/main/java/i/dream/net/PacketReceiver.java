package i.dream.net;

import i.dream.raft.cluster.message.PayloadMeta;

import java.nio.channels.SocketChannel;

public class PacketReceiver {

    public PacketReceiver(PayloadMeta payload, SocketChannel channel ) {
        this.payload = payload;
        this.channel = channel;
    }

    private PayloadMeta payload;

    private SocketChannel channel;


    public PayloadMeta getPayload() {
        return payload;
    }

    public SocketChannel getChannel() {
        return channel;
    }
}
