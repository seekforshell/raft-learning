package i.dream.raft.struct.cluster;

import lombok.Data;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
@Data
public class ClusterState {
    // Persistent state on all servers
    private Long currentTerm = -1L;
    private Long voteFor;

    private ClusterLog[] logs;

    // Volatile state on all servers
    private Long commitIndex = -1L;
    private Long lastApplied = -1L;

    private ClusterLeaderState leaderState = new ClusterLeaderState();

}
