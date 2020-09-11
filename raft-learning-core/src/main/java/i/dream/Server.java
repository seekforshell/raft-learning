package i.dream;

import i.dream.ex.ClusterException;
import i.dream.net.SelectorProxy;
import i.dream.raft.struct.cluster.ClusterInfo;
import i.dream.raft.struct.cluster.ClusterState;
import i.dream.raft.struct.node.NodeInfo;
import i.dream.util.FileUtil;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class Server implements IServer {

    private ClusterInfo clusterInfo;
    private ServerSocketChannel serverSocketChannel = null;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    public static final Object serverStartLock = new Object();

    public Server() {
        // init struct
        clusterInfo = new ClusterInfo();
        clusterInfo.setNodes(new ConcurrentHashMap<String, NodeInfo>(10));
        clusterInfo.setRole(NodeInfo.FOLLOWER);
        final ClusterState state = new ClusterState();
        clusterInfo.setState(state);
    }

    public void start () {

        SocketAddress socketAddress = new InetSocketAddress(FileUtil.getServerPort());
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(socketAddress);
            // register to selector
            serverSocketChannel.register(SelectorProxy.getSelector(), SelectionKey.OP_ACCEPT);

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

        synchronized (Server.serverStartLock) {
            serverStartLock.notifyAll();
        }

        logger.info("raft server is started.");
    }
}
