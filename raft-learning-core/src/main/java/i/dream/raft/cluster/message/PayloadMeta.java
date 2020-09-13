package i.dream.raft.cluster.message;

import java.io.Serializable;
import lombok.Data;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
@Data
public abstract class PayloadMeta implements Serializable {
    private int magic;  /* 负载的魔术字用于检验报文合法性 */
    private short type; /* 命令类型/消息类型 */
    private int flag;   /* 保留字段：进行位操作 */
    private long total; /* 报文长度 */
    private long digest; /* 摘要信息：取以上所有字段做因子 */
}
