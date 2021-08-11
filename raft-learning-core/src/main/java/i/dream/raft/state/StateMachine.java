package i.dream.raft.state;

import i.dream.raft.cluster.message.handler.PacketHandler;
import i.dream.raft.struct.cluster.ClusterInfo;
import i.dream.raft.struct.node.NodeInfo;

import java.util.Map;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class StateMachine {

	private ClusterInfo clusterInfo;

	public class ElectionTimeoutPacketHandler implements PacketHandler {
		private long timeout;

		ElectionTimeoutPacketHandler(long timeout) {
			this.timeout = timeout;
		}

		@Override
		public void run() {
			if (StateEnum.CANDIDATE.id == clusterInfo.getRole()) {
				// send vote && wait
				for (Map.Entry<String, NodeInfo> entry : clusterInfo.getNodes().entrySet()) {
					NodeInfo node = entry.getValue();
					if (node.getSocketChannel().isConnected()) {

					}
				}
			} else if (StateEnum.FOLLOWER.id == clusterInfo.getRole()) {

			}

		}
	}

	public enum StateEnum {
		CANDIDATE(0),
		FOLLOWER(1),
		LEADER(2);
		private int id;
		StateEnum(int id) {
			this.id = id;
		}
	}

	public class StateHandler<S, T, ACTION extends PacketHandler> {
		private S s;
		private T t;
		private ACTION action;

		StateHandler(S s, T t, ACTION action) {
			this.s = s;
			this.t = t;
			this.action = action;
		}

		public boolean match(S s, T t) {
			return this.s.equals(s) && this.t.equals(t);
		}

		public ACTION getAction() {
			return action;
		}
	}
}
