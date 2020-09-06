package i.dream.raft.struct.cluster;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class ClusterLeaderState {
    private Map<String, Long> nextIndex = new ConcurrentHashMap<>(12);
    private Map<String, Long> matchIndex = new ConcurrentHashMap<>(12);
}
