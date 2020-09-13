package i.dtream.client;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class RaftClientTest {

    @Test
    public void testA() {
        String[] args = new String[]{"--server", "127.0.0.1:1001", "--set", "test", "hello, world!"};
        RaftClient.main(args);
    }
}