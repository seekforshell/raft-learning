package i.dream.raft.cluster;

import i.dream.Server;
import i.dream.ex.ClusterException;
import i.dream.net.NetProcess;
import i.dream.util.RaftConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 */
public class ClusterServer implements Server {
    private ServerSocketChannel serverSocketChannel = null;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    public void start () {

        SocketAddress socketAddress = new InetSocketAddress(RaftConf.getClusterServerPort());
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(socketAddress);
            // register to selector
            serverSocketChannel.register(NetProcess.getSelector(), SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            logger.error("open selector error:", e);
            throw new ClusterException(e.getMessage());
        } catch (IllegalArgumentException ie) {
            logger.error("illegal argument error:", ie);
            throw new ClusterException(ie.getMessage());
        } catch (Exception e) {
            logger.error("unknown error:", e);
            throw new ClusterException(e.getMessage());
        }

        logger.info("cluster server is started.");
    }

}
