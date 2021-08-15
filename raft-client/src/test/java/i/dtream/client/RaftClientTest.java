package i.dtream.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import i.dream.raft.cluster.message.PayLoad;
import i.dream.raft.cluster.message.PayloadMeta;
import org.junit.Test;

import static i.dream.raft.cluster.message.MessageType.CLUSTERMSG_TYPE_TEST;

public class RaftClientTest {

    @Test
    public void testA() throws InterruptedException {
        String[] args = new String[]{"--server", "localhost:1901"};
        RaftClient.main(args);
        PayLoad.HelloPayLoad helloPayLoad = new PayLoad.HelloPayLoad();
        helloPayLoad.setLen(22);
        helloPayLoad.setType((short) CLUSTERMSG_TYPE_TEST.getCode());
        helloPayLoad.setContent("hello raft");
        RaftClient.send(helloPayLoad);

        synchronized (SocketManager.lock) {
            SocketManager.lock.wait();
        }

    }
}