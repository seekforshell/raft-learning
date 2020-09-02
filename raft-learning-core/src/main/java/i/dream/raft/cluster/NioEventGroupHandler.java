package i.dream.raft.cluster;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 * Create Date Time: 2020-09-02 17:08.
 */
public class NioEventGroupHandler {
    private final String MAGIC_V ="0x0902";

    public static LinkedBlockingQueue<SelectionKey> eventQueue = new LinkedBlockingQueue(1024);

    public boolean dealEvent() throws IOException {
        while (true) {
            SelectionKey event = eventQueue.poll();
            if (null != event) {
                if (event.isReadable()) {
                    SocketChannel channel = (SocketChannel) event.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
                    int c = channel.read(byteBuffer);
                } else {

                }
            }
        }
    }
}
