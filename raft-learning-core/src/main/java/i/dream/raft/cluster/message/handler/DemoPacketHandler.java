package i.dream.raft.cluster.message.handler;

import i.dream.raft.cluster.message.PayLoad;
import i.dream.raft.cluster.message.PayloadMeta;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class DemoPacketHandler extends AbstractPacketHandler {
	private PayLoad.CommandPayLoad commandPayLoad;
	public DemoPacketHandler(SocketChannel channel, PayloadMeta payload) {
		super(channel, payload);
		commandPayLoad = (PayLoad.CommandPayLoad) payload;
	}

	@Override
	public void run() {
		System.out.println(commandPayLoad.toString());
	}
}
