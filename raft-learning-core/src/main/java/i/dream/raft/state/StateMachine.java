package i.dream.raft.state;

import i.dream.raft.cluster.message.handler.PacketHandler;
import i.dream.raft.struct.cluster.ClusterInfo;
import i.dream.util.RaftConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class StateMachine extends Thread {

	private Logger logger = LoggerFactory.getLogger(StateMachine.class);
	private ClusterInfo clusterInfo;

	private RaftConf raftConf;

	/**
	 * 心跳超时时间
	 */
	private final int HEAT_BEAT_TO = 10;

	public StateMachine(ClusterInfo clusterInfo) {
		this.clusterInfo = clusterInfo;
	}

	private boolean isTimeout(long last) {
		if (-1 == last || (System.currentTimeMillis() - last) - 1000* HEAT_BEAT_TO > 0) {
			return true;
		}

		return false;
	}

	@Override
	public void run() {

		StateHandlerBuilder.StateHandler stateHandler = StateHandlerBuilder.builder();
		for ( ; ;) {
			if (StateEnum.FOLLOWER == clusterInfo.getRole()) {

				if (!clusterInfo.leaderReady() || isTimeout(clusterInfo.getLastHeartBeatFromLeader())) {
					logger.info("heart beat timeout with leader");

					clusterInfo.setRole(StateEnum.CANDIDATE);
					StateHandlerBuilder.StateEvent stateEvent = new StateHandlerBuilder.StateEvent(StateEnum.FOLLOWER, StateEnum.CANDIDATE);
					stateHandler.match(stateEvent).callback();

				}
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.warn("interrupt state machine, ignored");
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
