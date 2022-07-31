package i.dream.raft.state;

import i.dream.net.NetProcess;
import i.dream.util.RaftConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class LeaderElect {
	private static Logger logger = LoggerFactory.getLogger(LeaderElect.class);
	public LeaderElect() {
	}

	public void elect() {
		try {
			// connect
			Set<InetSocketAddress> nodeSet = RaftConf.getClusterInfo();
			for (Iterator<InetSocketAddress> it = nodeSet.iterator(); it.hasNext(); ) {
				InetSocketAddress nodeAddr = it.next();
				SocketChannel nodeChannel = SocketChannel.open();
				nodeChannel.configureBlocking(false);
				nodeChannel.connect(nodeAddr);
				NetProcess.registerChannel(nodeChannel, SelectionKey.OP_READ);
			}
		} catch (Exception e) {
			logger.error("elect error", e);
		}
	}
}
