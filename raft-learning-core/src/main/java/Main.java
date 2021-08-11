import i.dream.RaftServer;
import i.dream.net.NetProcess;
import i.dream.util.FileUtil;

import java.util.Properties;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 */
public class Main {

    private static String printHelp() {
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("");

        return helpMessage.toString();
    }

    public static void main(String[] args) throws Exception {

        Properties config = FileUtil.readConfig();
        String addressList = config.getProperty("bind");
        if (null == addressList || addressList.length() < 1) {
            throw new IllegalArgumentException("please init `bind` parameters");
        }

        if (addressList.split(":").length != 2) {
            throw new IllegalArgumentException("invalid init `bind` parameter");

        }

        // selector init
        NetProcess netProcess = new NetProcess();
        netProcess.init();

        // raft server daemon
        RaftServer raftServer = new RaftServer();
        raftServer.start();

    }
}
