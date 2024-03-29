package i.dream.raft.cluster.message;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public enum MessageType {

    CMD_TYPE_INFO(100, "info"),
    CMD_TYPE_SET(101, "set"),
    CMD_TYPE_GET(102, "get"),

    /**
     * 这里参考redis的消息类型
     */

    CLUSTERMSG_TYPE_TEST(-1, "test"),
    CLUSTERMSG_TYPE_PING(0, "Ping"),
    CLUSTERMSG_TYPE_PONG(1, "Pong (reply to Ping)"),
    CLUSTERMSG_TYPE_MEET(2, "Meet let's join message"),
    CLUSTERMSG_TYPE_FAIL(3, "Mark node xxx as failing"),
    CLUSTERMSG_TYPE_PUBLISH(4, "Pub/Sub Publish propagation"),
    CLUSTERMSG_TYPE_FAILOVER_AUTH_REQUEST(5, "May I failover?"),
    CLUSTERMSG_TYPE_FAILOVER_AUTH_ACK(6, "Yes, you have my vote."),
    CLUSTERMSG_TYPE_UPDATE(7 ,"Another node slots configuration"),
    CLUSTERMSG_TYPE_MFSTART(8, "Pause clients for manual failover"),
    CLUSTERMSG_TYPE_MODULE(9, "Module cluster API message."),
    CLUSTERMSG_TYPE_COUNT(10, "Total number of message types."),
    CLUSTERMSG_TYPE_VOTE(11, "request for vote message types.");

    private int code;
    private String message;

    MessageType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }
}
