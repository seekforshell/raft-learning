package i.dream.raft.struct.cluster;

import i.dream.raft.struct.node.NodeInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 * Create Date Time: 2020-08-24 19:58.
 */
@Data
public class ClusterInfo implements Serializable {
    private Map<String, NodeInfo> nodes;

    private ClusterState state;

    // leader,candidate or follower
    private short role;
}
