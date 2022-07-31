package i.dream.raft.cluster.heartbeat;

import i.dream.RaftServer;
import i.dream.net.NetProcess;
import i.dream.raft.cluster.NetPacketHandler;
import i.dream.raft.cluster.message.HeartBeatMessage;
import i.dream.raft.struct.node.NodeInfo;
import i.dream.util.RaftConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class NodeHeartBeat extends Thread{

    private RaftServer raftServer;

    private Logger logger = LoggerFactory.getLogger(NodeHeartBeat.class);

    public NodeHeartBeat(RaftServer raftServer) {
        this.raftServer = raftServer;
    }

    @Override
    public void run() {

        try {
            for (InetSocketAddress node : RaftConf.getClusterInfo()) {
                if (!raftServer.getClusterInfo().getNodeStat().get(node)) {
                    SocketChannel nodeChannel = SocketChannel.open();
                    nodeChannel.configureBlocking(false);
                    nodeChannel.connect(node);
                    NetProcess.registerChannel(nodeChannel, SelectionKey.OP_READ);
                } else {
                    NodeInfo nodeInfo = raftServer.getClusterInfo().getConnectedNodes().get(node);
                    HeartBeatMessage heartBeatMessage = new HeartBeatMessage();
                    heartBeatMessage.setContent("I'm alive".getBytes());
                    SocketChannel nodeChannel = nodeInfo.getSocketChannel();
                    NetPacketHandler.sendQueueMap.get(nodeChannel).add(heartBeatMessage);
                    NetProcess.registerChannel(nodeChannel, SelectionKey.OP_WRITE);
                }
            }


            Thread.sleep(100);
        } catch (Exception e) {
            logger.error("error", e);
        }

    }
}
