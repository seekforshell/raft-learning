package i.dream.raft.cluster.process;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class GossipProcessImpl extends AbstractProcess implements GossipProcess {

    public GossipProcessImpl(SocketChannel channel, SelectionKey key) {
        super(channel, key);
    }

    @Override
    public void run() {

    }
}
