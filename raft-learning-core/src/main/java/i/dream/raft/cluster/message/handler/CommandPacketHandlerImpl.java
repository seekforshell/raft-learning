package i.dream.raft.cluster.message.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandPacketHandlerImpl implements PacketHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private SocketChannel client;

    public CommandPacketHandlerImpl(SocketChannel client) {
        this.client = client;
    }

    @Override public void run() {
        String echo = "hello client";
        ByteBuffer buffer = ByteBuffer.wrap(echo.getBytes());
        try {
            client.write(buffer);
        } catch (IOException e) {
            log.error("write error:", e);
        }
    }
}
