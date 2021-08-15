package i.dream.raft.cluster.message;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import i.dream.raft.Types;
import lombok.Data;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
@Data
public class PayloadMeta extends Types implements Serializable {
    private final int metaSize = 22;
    private int len = 0;
    private int magic = 0xfefefefe;  /* 负载的魔术字用于检验报文合法性 */
    private short type; /* 命令类型/消息类型 */
    private int flag;   /* 保留字段：进行位操作 */
    private long digest; /* 摘要信息：取以上所有字段做因子 */

    @Override
    public ByteBuffer write(ByteBuffer buffer) {
        PayloadMeta payloadMeta = this;
        buffer.putInt(metaSize);
        buffer.putInt(PayLoad.MAGIC);
        buffer.putShort(payloadMeta.type);
        buffer.putInt(payloadMeta.flag);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            buffer.putLong(0L);
        } catch (NoSuchAlgorithmException e) {
            //
        }
        len += 22;

        return buffer;
    }

    @Override
    public Object read(ByteBuffer buffer) {
        PayloadMeta payloadMeta = this;
        payloadMeta.setLen(buffer.getInt());
        payloadMeta.setMagic(buffer.getInt());
        payloadMeta.setType(buffer.getShort());
        payloadMeta.setFlag(buffer.getInt());
        payloadMeta.setDigest(buffer.getLong());
        return payloadMeta;
    }


    public static PayloadMeta parse(ByteBuffer buffer) {
        PayloadMeta payloadMeta = new PayloadMeta();
        payloadMeta.setLen(buffer.getInt());
        payloadMeta.setMagic(buffer.getInt());
        payloadMeta.setType(buffer.getShort());
        payloadMeta.setFlag(buffer.getInt());
        payloadMeta.setDigest(buffer.getLong());
        return payloadMeta;
    }
}
