package i.dtream.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static i.dtream.client.RaftClient.commandMap;
import static i.dtream.client.RaftClient.usage;

public class CommandParser {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    Map<String, Object> options = new HashMap<>();

    Pattern serverAddrPattern = Pattern.compile("\\S+:\\d+");
    Pattern keyPattern = Pattern.compile("\\w+");
    Pattern valuePattern = Pattern.compile("[^\\S+]");

    public Map<String, Object> parseOptions(String args[]) {
        if (null == args || args.length == 0) {
            logger.warn("parse option, args is empty.");
            usage();
            System.exit(1);
        }

        for (int i = 0; i < args.length;) {
            String e = args[i];
            try {
                if (e.startsWith("--")) {
                    if (e.length() > 2) {
                        String command = e.substring(2);
                        if (command.equals("set")) {

                            String key = args[i + 1];
                            String keyValue = args[i + 2];
                            if (!keyPattern.matcher(key).matches() || !valuePattern.matcher(keyValue).matches()) {
                                System.out.println("非法的参数：" + commandMap.get("set"));
                                System.exit(1);
                            }
                            Map<String, String> kv = new HashMap<>();
                            kv.put(key, keyValue);
                            options.put(command, kv);
                            i += 3;
                        } else if (command.equals("server")) {
                            String serverInfo = args[i + 1];
                            if (!serverAddrPattern.matcher(serverInfo).matches()) {
                                System.out.println("非法的参数：" + commandMap.get("server"));
                                System.exit(1);
                            }

                            options.put(command, serverInfo);
                            i += 2;
                        }
                    } else {
                        throw new IllegalArgumentException("illegal argument.");
                    }
                } else {
                    throw new IllegalArgumentException("illegal argument: command must be prefixed with '--'");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                usage();
                System.exit(1);
            }
        }

        return options;
    }

}
