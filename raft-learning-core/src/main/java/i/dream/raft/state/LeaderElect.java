package i.dream.raft.state;

import i.dream.raft.struct.cluster.ClusterInfo;
import i.dream.util.RaftConf;

import java.net.InetSocketAddress;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class LeaderElect {

	private static RaftConf raftConf;

	public static void elect() {
		try {
			for (InetSocketAddress addr : raftConf.getClusterInfo()) {
				// start elect
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
