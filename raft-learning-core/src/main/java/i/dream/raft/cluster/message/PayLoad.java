package i.dream.raft.cluster.message;

import i.dream.raft.struct.cluster.ClusterLog;
import lombok.Data;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PayLoad {
    public static final int MAGIC = 0x20202020;

    @Data
    public static class HelloPayLoad extends PayloadMeta {

        private String content = "";

        @Override
        public ByteBuffer write(ByteBuffer buffer) {
            byte[] encodeContent = Base64.getEncoder().encode(content.getBytes());
            buffer = ByteBuffer.allocate(encodeContent.length + getMetaSize());

            super.write(buffer);
            if (null != content) {
                buffer.put(encodeContent);

                // calculate packet size
                buffer.putInt(0, encodeContent.length + getMetaSize());
                buffer.rewind();
            }

            return buffer;
        }

        @Override
        public Object read(ByteBuffer buffer) {
            buffer.rewind();
            super.read(buffer);
            buffer.position(getMetaSize());

            String content = new String(Base64.getDecoder().decode(buffer).array());

            this.content = content;

            return this;
        }
    }

    @Data
    public static class CommandPayLoad extends PayloadMeta {
        private Map<String, String> content = new HashMap<>(16); /* 命令所携带的参数信息 */

        @Override
        public ByteBuffer write(ByteBuffer buffer) {
            super.write(buffer);
            // todo need to serialize map using objectmapper
            byte[] encodeContent = Base64.getEncoder().encode(content.toString().getBytes());
            buffer.put(encodeContent);
            int packetSize = encodeContent.length + getMetaSize();

            // calculate packet size
            buffer.rewind();
            buffer.putInt(packetSize, 4);
            buffer.rewind();

            return buffer;
        }

        @Override
        public Object read(ByteBuffer buffer) {
            // TODO: 2021/8/15
            CommandPayLoad commandPayLoad = (CommandPayLoad) super.read(buffer);
            int contentSize = commandPayLoad.getLen() - commandPayLoad.getMetaSize();
            ByteBuffer contentBuffer = ByteBuffer.allocate(contentSize);
            buffer.get(contentBuffer.array());
            String jsonString = Base64.getDecoder().decode(contentBuffer).toString();

            return commandPayLoad;
        }
    }

    /**
     * ping/pong payload
     */
    @Data
    public class ClusterPingPayLoad extends PayloadMeta {
        private long currentTerm;
        private byte[] leaderClusterId;
        private long prevLogIndex;
        private long prevLogTerm;
        private ClusterLog[] logs;

        @Override
        public ByteBuffer write(ByteBuffer buffer) {
            super.write(buffer);
            return buffer;
        }

        @Override
        public Object read(ByteBuffer buffer) {
            return null;
        }
    }

    /**
     * vote payload
     */
    public class RequestVotePayLoad extends PayloadMeta {
        /**
         * candidate’s term
         */
        private long candidateTerm;
        /**
         * candidate requesting vote
         */
        private byte[] candidateId;

        private long lastLogIndex;

        private long lastLogTerm;
    }
}
