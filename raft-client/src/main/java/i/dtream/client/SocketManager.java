package i.dtream.client;

import i.dream.ex.ConnectError;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketManager {
    protected Socket s = null;
    public byte[] buffer;


    protected ByteBuffer byteBuffer = ByteBuffer.allocate(2 * 1024 * 1024);

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public void init(String serverIp , int serverPort) {
        s = new Socket();
        try {
            s.connect(new InetSocketAddress(serverIp, serverPort));
        } catch (IOException e) {
            logger.error("connect error", e);
            throw new ConnectError("connect server error.");
        }

        Thread thread = new Thread(new RequestTask());
        thread.start();

        logger.info("request task is started.");
    }

    class RequestTask implements Runnable {

        @Override
        public void run() {
            for (;s.isConnected();) {
                InputStream inputStream = null;
                try {
                    inputStream = s.getInputStream();
                    while (-1 != inputStream.read(buffer)) {
                        if (byteBuffer.hasRemaining()) {
                            logger.error("the buffer is overflow!");
                        }
                    }

                    byteBuffer.rewind();

                    CharBuffer content = Charset.defaultCharset().decode(byteBuffer);
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
