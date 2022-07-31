package i.dream.client;

import i.dream.ex.ConnectError;
import i.dream.raft.cluster.message.PayLoad;
import i.dream.raft.cluster.message.PayloadMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SocketManager {
    protected SocketChannel s = null;

    private static BlockingQueue<PayloadMeta> sendQueue = new ArrayBlockingQueue<>(100);

    protected ByteBuffer recvBuffer = ByteBuffer.allocate(2 * 1024 * 1024);

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final Object lock = new Object();

    public void init(String serverIp , int serverPort) {
        try {
            s = SocketChannel.open();
            s.configureBlocking(true);
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
			try {
				for (; s.isConnected(); ) {
					try {
						logger.info("client channel:{}", s.getLocalAddress());
						// write
						PayLoad.HelloPayLoad sendPayload = (PayLoad.HelloPayLoad) sendQueue.take();
						try {
							ByteBuffer packetBuffer = sendPayload.write(null);
							int wl = s.write(packetBuffer);

							logger.info("write {} byte", wl);
						} catch (Exception e) {
							logger.error("write error", e);
						}

						while (-1 != s.read(recvBuffer)) {
							if (recvBuffer.hasRemaining()) {
								logger.error("the buffer is overflow!");
							}
						}

						recvBuffer.rewind();

						logger.info("receive package:" + recvBuffer.toString());

						Thread.sleep(100);
					} catch (IOException io) {
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
			} catch (Exception e) {
				logger.error("request task error", e);
			} finally {
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		}
    }
}
