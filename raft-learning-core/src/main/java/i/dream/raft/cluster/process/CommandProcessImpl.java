package i.dream.raft.cluster.process;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandProcessImpl implements Process {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private SocketChannel client;

    public CommandProcessImpl(SocketChannel client) {
        this.client = client;
    }

    @Override public void process() {
        String echo = "hello client";
        ByteBuffer buffer = ByteBuffer.wrap(echo.getBytes());
        try {
            client.write(buffer);
        } catch (IOException e) {
            log.error("write error:", e);
        }
    }

    @Override public void run() {
        process();
    }
}
