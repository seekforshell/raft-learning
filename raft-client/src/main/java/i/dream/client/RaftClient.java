package i.dream.client;

import i.dream.raft.cluster.message.PayloadMeta;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RaftClient {
    public static Map<String, String> commandMap = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static SocketManager socketManager = null;

    static {
        commandMap.put("server", "host:port");
        commandMap.put("quit","");
        commandMap.put("set","set key value");
        commandMap.put("get","get key");
        commandMap.put("info","info");
    }

    public static void usage() {
        System.err.println("raft-client --server host:port cmd args");
        for (String cmd : commandMap.keySet()) {
            System.err.println("\t"+cmd+ " " + commandMap.get(cmd));
        }
    }

    public static void send(PayloadMeta payloadMeta) {
        socketManager.send(payloadMeta);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            System.exit(1);
        }

        CommandParser parser = new CommandParser();
        Map<String, Object> options = parser.parseOptions(args);

        String serverInfo = (String) options.get("server");
        String serverIp = serverInfo.split(":")[0];
        String serverPort = serverInfo.split(":")[1];

        socketManager = new SocketManager();
        socketManager.init(serverIp, Integer.parseInt(serverPort));

        log.info("welcome to use raft client!");
    }
}
