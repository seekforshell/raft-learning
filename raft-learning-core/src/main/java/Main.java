import i.dream.RaftServer;
import i.dream.net.NetProcess;
import i.dream.util.RaftConf;

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

        RaftConf.readConfig();

        // selector init
        NetProcess netProcess = new NetProcess();
        netProcess.init();

        // raft server daemon
        RaftServer raftServer = new RaftServer();
        raftServer.start();

    }
}
