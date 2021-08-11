package i.dtream.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import i.dream.raft.cluster.message.PayLoad;
import org.junit.Test;

import static i.dream.raft.cluster.message.MessageType.CLUSTERMSG_TYPE_TEST;

public class RaftClientTest {

    @Test
    public void testA() {
        String[] args = new String[]{"--server", "localhost:1901"};
        RaftClient.main(args);
        PayLoad.CommandPayLoad commandPayLoad = new PayLoad.CommandPayLoad();
        commandPayLoad.setLen((short) 100);
        commandPayLoad.setType((short) CLUSTERMSG_TYPE_TEST.getCode());
        commandPayLoad.setDigest(1000);
        Map<String, Object> content = new HashMap();
        content.put("k1", "v1");
        content.put("k2", "v2");
        commandPayLoad.setContent(content);

        RaftClient.send(commandPayLoad);

    }
}