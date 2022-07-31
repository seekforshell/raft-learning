package i.dream.client;

import i.dream.raft.cluster.message.PayLoad;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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