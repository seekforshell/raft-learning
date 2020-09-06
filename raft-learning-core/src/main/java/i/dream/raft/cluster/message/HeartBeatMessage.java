package i.dream.raft.cluster.message;

import lombok.Data;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
@Data
public class HeartBeatMessage extends MessageHeader {

    byte[] content;
}
