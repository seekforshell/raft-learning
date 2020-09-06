package i.dream.raft.cluster.message;

import lombok.Data;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
@Data
public abstract class MessageHeader {

    private int magic;

    private int msgType;

    private long total;

    private long crc;
}
