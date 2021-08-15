package i.dream.raft.cluster.message.handler;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
@FunctionalInterface
public interface PacketCallback {
	void handle(ByteBuffer rawPackage, SocketChannel channel);
}
