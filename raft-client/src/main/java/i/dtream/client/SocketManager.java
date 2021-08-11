package i.dtream.client;

import i.dream.ex.ConnectError;
import i.dream.raft.cluster.message.PayLoad;
import i.dream.raft.cluster.message.PayloadMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class SocketManager {
    protected SocketChannel s = null;

    private static Queue<PayloadMeta> sendQueue = new ArrayBlockingQueue<>(100);

    protected ByteBuffer recvBuffer = ByteBuffer.allocate(2 * 1024 * 1024);

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public void init(String serverIp , int serverPort) {
        try {
            s = SocketChannel.open();
            s.connect(new InetSocketAddress(serverIp, serverPort));
        } catch (IOException e) {
            logger.error("connect error", e);
            throw new ConnectError("connect server error.");
        }

        Thread thread = new Thread(new RequestTask());
        thread.setDaemon(true);
        thread.start();

        logger.info("request task is started.");
    }

    public void send(PayloadMeta payloadMeta) {
        if (null != payloadMeta) {
            sendQueue.add(payloadMeta);
        }
    }

    class RequestTask implements Runnable {

        @Override
        public void run() {
            for (;s.isConnected();) {
                try {

                    // write
                    PayLoad.CommandPayLoad sendPayload = (PayLoad.CommandPayLoad) sendQueue.poll();
                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(sendPayload);
                        s.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
                    }

                    while (-1 != s.read(recvBuffer)) {
                        if (recvBuffer.hasRemaining()) {
                            logger.error("the buffer is overflow!");
                        }
                    }

                    recvBuffer.rewind();

                    CharBuffer content = Charset.defaultCharset().decode(recvBuffer);
                    logger.info("receive package:" + content.toString());

                    Thread.sleep(100);
                } catch (IOException io){
                    logger.error("io error", io);
                } catch (InterruptedException e) {
                    logger.error("interrupt error", e);
                    Thread.currentThread().interrupt();
                } finally {
                    try {
                        s.close();
                    } catch (IOException e) {
                        logger.error("close error", e);
                    }
                }

            }
        }
    }
}
