package i.dream.raft.cluster.message.handler;

import i.dream.RaftServer;
import i.dream.raft.struct.node.NodeInfo;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class HeartBeatPacketHandler implements PacketHandler {

	private RaftServer raftServer;

	HeartBeatPacketHandler(RaftServer raftServer) {
		this.raftServer = raftServer;
	}

	@Override
	public void run() {

	}
}
