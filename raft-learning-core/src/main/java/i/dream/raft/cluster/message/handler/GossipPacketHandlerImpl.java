package i.dream.raft.cluster.message.handler;

import i.dream.raft.cluster.message.PayloadMeta;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class GossipPacketHandlerImpl extends AbstractPacketHandler implements GossipPacketHandler {

    public GossipPacketHandlerImpl(SocketChannel channel, PayloadMeta key) {
        super(channel, key);
    }

    @Override
    public void run() {

    }
}
