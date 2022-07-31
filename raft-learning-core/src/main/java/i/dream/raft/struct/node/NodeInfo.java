package i.dream.raft.struct.node;

import lombok.Data;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 * Create Date Time: 2020-08-24 20:00.
 */
@Data
public class NodeInfo {

    private String nodeId;

    private SocketChannel socketChannel;

    private volatile short channelStatus;
}
