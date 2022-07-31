package i.dream;

import i.dream.ex.ClusterException;
import i.dream.net.NetProcess;
import i.dream.raft.state.StateMachine;
import i.dream.raft.struct.cluster.ClusterInfo;
import i.dream.raft.struct.cluster.ClusterState;
import i.dream.raft.struct.node.NodeInfo;
import i.dream.util.RaftConf;
import lombok.Data;
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
@Data
public class RaftServer implements Server {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    public final Object serverStartLock = new Object();

    private ClusterInfo clusterInfo;
    private ServerSocketChannel serverSocketChannel = null;
    private NetProcess netProcess;

    public RaftServer(NetProcess netProcess) {
        this.netProcess = netProcess;
    }

    public void init() throws Exception {
        // init struct
        clusterInfo = new ClusterInfo();
        clusterInfo.setConnectedNodes(new ConcurrentHashMap<InetSocketAddress, NodeInfo>(10));
        clusterInfo.setRole(StateMachine.StateEnum.FOLLOWER);
        clusterInfo.setLastHeartBeatFromLeader(-1);
        final ClusterState state = new ClusterState();
        clusterInfo.setState(state);

        clusterInfo.setNodes(RaftConf.getClusterInfo());
    }


    public ClusterInfo getClusterInfo() {
        return clusterInfo;
    }

    public void start () {

        SocketAddress socketAddress = RaftConf.getRaftAddress();
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(socketAddress);
            // register to selector
            serverSocketChannel.register(netProcess.getSelector(), SelectionKey.OP_ACCEPT);


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

        synchronized (serverStartLock) {
            serverStartLock.notifyAll();
        }

        logger.info("raft server is started.");
    }
}
