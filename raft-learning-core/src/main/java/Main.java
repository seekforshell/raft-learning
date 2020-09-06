import i.dream.Server;
import i.dream.net.SelectorProxy;
import i.dream.raft.cluster.ClusterBootStrap;
import i.dream.raft.cluster.process.MainEventProcess;
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

        // Main event loop
        MainEventProcess mainProcess = new MainEventProcess();
        mainProcess.run();

        // selector init
        SelectorProxy selectorProxy = new SelectorProxy();
        selectorProxy.init();

        // raft server daemon
        Server server = new Server();
        server.start();

        // cluster daemon
        ClusterBootStrap clusterBootStrap = new ClusterBootStrap();
        clusterBootStrap.start();

    }
}
