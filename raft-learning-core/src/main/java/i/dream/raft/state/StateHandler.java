package i.dream.raft.state;

import i.dream.raft.state.StateMachine.StateEnum;
import i.dream.util.RaftConf;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static i.dream.raft.state.StateMachine.StateEnum.CANDIDATE;
import static i.dream.raft.state.StateMachine.StateEnum.FOLLOWER;
import static i.dream.raft.state.StateMachine.StateEnum.LEADER;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class StateHandler {

	private static List<StateTriple> stateTriples = new ArrayList<>();
	private RaftConf raftConf;

	static {
		stateTriples.add(new StateTriple(FOLLOWER, CANDIDATE, LeaderElect::elect));
		stateTriples.add(new StateTriple(CANDIDATE, CANDIDATE, LeaderElect::elect));
		stateTriples.add(new StateTriple(CANDIDATE, LEADER, LeaderElect::elect));
	}

	public static class StateTriple {
		StateEnum before;
		StateEnum after;
		StateTransformCB cb;
		StateTriple(StateEnum b, StateEnum a, StateTransformCB cb) {
			this.before = b;
			this.after = a;
			this.cb = cb;
		}
	}
}
