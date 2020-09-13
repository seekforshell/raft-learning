package i.dream.raft.cluster.message;

import i.dream.raft.struct.cluster.ClusterLog;
import java.io.Serializable;
import java.util.Map;

public class PayLoad {
    public static final int MAGIC = 0x20202020;

    public class CommandPayLoad extends PayloadMeta {
        private Map<String, Object> content; /* 命令所携带的参数信息 */

    }

    /**
     * ping/pong payload
     */
    public class ClusterPingPayLoad extends PayloadMeta {
        private long currentTerm;
        private byte[] leaderClusterId;
        private long prevLogIndex;
        private long prevLogTerm;
        private ClusterLog[] logs;

    }

    /**
     * vote payload
     */
    public class VotePayLoad extends PayloadMeta {
        private long candidateTerm;
        private byte[] candidateId;
        private long lastLogIndex;
        private long lastLogTerm;
    }
}
