package i.dream.raft.struct.cluster;

import i.dream.raft.state.StateMachine;
import i.dream.raft.struct.node.NodeInfo;
import lombok.Data;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 * Create Date Time: 2020-08-24 19:58.
 */
@Data
public class ClusterInfo implements Serializable {
    /**
     * 已经建立连接的
     */
    private Map<InetSocketAddress, NodeInfo> connectedNodes;

    private Set<InetSocketAddress> nodes;

    private Map<InetSocketAddress, Boolean> nodeStat;

    /**
     * leader info
     */
    private SocketChannel leader;

    private ClusterState state;

    // leader,candidate or follower
    private volatile StateMachine.StateEnum role;

    /**
     * 上次接收到的leader信息
     */
    private long lastHeartBeatFromLeader = -1L;

    public boolean leaderReady() {
        if (null == leader || !leader.isOpen()) {
            return false;
        }

        return true;
    }
}
