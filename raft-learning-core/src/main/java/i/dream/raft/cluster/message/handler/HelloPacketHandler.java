package i.dream.raft.cluster.message.handler;

import i.dream.raft.cluster.message.PayLoad;
import i.dream.raft.cluster.message.PayloadMeta;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class HelloPacketHandler extends AbstractPacketHandler {
	private PayLoad.HelloPayLoad commandPayLoad;
	public HelloPacketHandler(SocketChannel channel, PayloadMeta payload) {
		super(channel, payload);
		commandPayLoad = (PayLoad.HelloPayLoad) payload;
	}

	@Override
	public void run() {
		System.out.println(commandPayLoad.getContent());
	}
}
