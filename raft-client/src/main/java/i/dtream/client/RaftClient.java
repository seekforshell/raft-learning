package i.dtream.client;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaftClient {
    public static Map<String, String> commandMap = new HashMap<>();
     Logger logger = LoggerFactory.getLogger(this.getClass());

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

        SocketManager socketManager = new SocketManager();
        socketManager.init(serverIp, Integer.parseInt(serverPort));

        System.out.println("welcome to use raft client!");
        CommandDispatcher executor = new CommandDispatcher();
        executor.exec();

    }
}
