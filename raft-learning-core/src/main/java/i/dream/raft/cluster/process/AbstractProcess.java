package i.dream.raft.cluster.process;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class AbstractProcess implements Process {

    private SocketChannel channel;

    private SelectionKey key;

    private ByteBuffer toSentBuffer;

    AbstractProcess(SocketChannel channel, SelectionKey key) {
        this.channel = channel;
        this.key = key;
    }

    @Override
    public void run() {

    }

    public ByteBuffer getToSentBuffer() {
        return toSentBuffer;
    }
}
