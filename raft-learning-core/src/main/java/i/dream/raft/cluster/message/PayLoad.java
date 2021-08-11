package i.dream.raft.cluster.message;

import i.dream.raft.struct.cluster.ClusterLog;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

public class PayLoad {
    public static final int MAGIC = 0x20202020;

    @Data
    public static class CommandPayLoad extends PayloadMeta {
        private Map<String, Object> content; /* 命令所携带的参数信息 */

    }

    /**
     * ping/pong payload
     */
    @Data
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
    public class RequestVotePayLoad extends PayloadMeta {
        /**
         * candidate’s term
         */
        private long candidateTerm;
        /**
         * candidate requesting vote
         */
        private byte[] candidateId;

        private long lastLogIndex;

        private long lastLogTerm;
    }
}
