package i.dream.util;

import i.dream.ex.ConfigException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 * Create Date Time: 2020-08-31 10:24.
 */
@Slf4j
public class RaftConf {
//    private static final String filePath = "/opt/conf/raft.properties";
    private static final String filePath = "M:\\github\\raft-learning\\conf\\raft.properties";

    // cluster_port = [server_port] + 10000
    private static final Integer RAFT_SERVER_PORT = 1001;
    private static final Integer CLUSTER_SERVER_PORT = 10000 + RAFT_SERVER_PORT;

    private static final String RAFT_PORT = "raft.port";

    private static final String RAFT_ADDRESS = "raft.address";

    private static Properties raftProperties = new Properties();

    public static void readConfig() throws Exception {
        InputStream fileInput = null;
        try {
            String confPath = Optional.ofNullable(System.getProperty("conf")).orElseGet(
                    () -> RaftConf.class.getProtectionDomain().getClassLoader().getResource("raft.properties").getPath());
            fileInput = new FileInputStream(new File(confPath));
            raftProperties.load(fileInput);
        } catch (FileNotFoundException nx) {
            throw new Exception("file not exist, please specify the position of config file!");
        } catch (IOException e) {
            log.error("load conf error", e);
            throw new ConfigException("read conf exception:%s", e.getMessage());
        } finally {
            if (null != fileInput) {
                try {
                    fileInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String getClusterIp() {

        String serverAddress = null;

        if (null != (serverAddress = raftProperties.getProperty(RAFT_ADDRESS))) {
            return serverAddress.split(":")[0];
        }

        return "localhost" ;
    }

    public static Integer getClusterServerPort() {

        String serverPort = raftProperties.getProperty(RAFT_PORT);
        return Integer.parseInt(serverPort) + 10000;

    }

    public static Integer getRaftServerPort() {
        return (Integer) raftProperties.getOrDefault(RAFT_PORT, RAFT_SERVER_PORT);

    }

    public static InetSocketAddress getRaftAddress() {
        InetSocketAddress inetSocketAddress = null;
        if (null != raftProperties) {
            int serverPort = Integer.parseInt((String) raftProperties.get(RAFT_PORT));
            String serverAddress = raftProperties.getProperty(RAFT_ADDRESS);
            inetSocketAddress = new InetSocketAddress(serverAddress, serverPort);
        }

        Optional.ofNullable(inetSocketAddress).orElseThrow(
                () -> new IllegalArgumentException("no raft address info!"));

        return inetSocketAddress;
    }

    public static Set<InetSocketAddress> getClusterInfo() throws Exception {
        Set nodeSet = new HashSet();
        String clusterList = raftProperties.getProperty("cluster");
        for (String node : clusterList.split(",")) {
            String[] nodeIpPort = node.trim().split(":");
            if (2 != nodeIpPort.length) {
                throw new IllegalArgumentException("illegal host info format");
           }

            nodeSet.add(new InetSocketAddress(nodeIpPort[0], Integer.parseInt(nodeIpPort[1])));
        }

        return nodeSet;
    }
}
