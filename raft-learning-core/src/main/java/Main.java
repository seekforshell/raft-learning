import i.dream.raft.cluster.ClusterBootStrap;
import i.dream.util.FileUtil;

import java.util.Properties;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 * Create Date Time: 2020-08-24 19:56.
 */
public class Main {

    private static String printHelp() {
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("");

        return helpMessage.toString();
    }
    public static void main(String[] args) throws Exception {
//        if (null == args || args.length < 1) {
//            throw new IllegalArgumentException("");
//        }

        Properties config = FileUtil.readConfig();
        String addressList = config.getProperty("bind");
        if (null == addressList || addressList.length() < 1) {
            throw new IllegalArgumentException("please init `bind` parameters");
        }

        if (addressList.split(":").length != 2) {
            throw new IllegalArgumentException("invalid init `bind` parameter");

        }

        String ip = addressList.split(":")[0];
        int port = Integer.parseInt(addressList.split(":")[1]);

        ClusterBootStrap clusterBootStrap = new ClusterBootStrap();
        clusterBootStrap.start();

    }
}
