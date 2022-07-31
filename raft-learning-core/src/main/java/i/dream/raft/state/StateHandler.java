package i.dream.raft.state;

import i.dream.raft.state.StateMachine.StateEnum;
import i.dream.util.RaftConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static i.dream.raft.state.StateMachine.StateEnum.CANDIDATE;
import static i.dream.raft.state.StateMachine.StateEnum.FOLLOWER;
import static i.dream.raft.state.StateMachine.StateEnum.LEADER;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
class StateHandlerBuilder {

	private static Logger log = LoggerFactory.getLogger(StateHandlerBuilder.class);

	private static volatile StateHandler handler = null;

	private static List<StateTriple> stateTriples = new ArrayList<>();
	private BlockingQueue<StateEvent> eventQueue = new LinkedBlockingQueue<>();


	public static StateHandler builder() {
		return getInstance();
	}

	public static class StateEvent {
		StateEnum before;
		StateEnum after;
		public StateEvent(StateEnum before, StateEnum after) {
			this.before = before;
			this.after = after;
		}
	}

	public static StateHandler getInstance() {
		if (null == handler) {
			handler = new StateHandler();
			handler.init();
		}

		return handler;
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

	public static class StateHandler {
		public void init() {
			LeaderElect leaderElect = new LeaderElect();
			stateTriples.add(new StateTriple(FOLLOWER, CANDIDATE, leaderElect::elect));
		}

		StateTransformCB match(StateEvent event) {
			StateTransformCB cb = null;
			for (StateTriple triple : stateTriples) {
				if (triple.after == event.after && triple.before == event.before) {
					cb = triple.cb;
					break;
				}
			}

			return cb;
		}

	}

}
