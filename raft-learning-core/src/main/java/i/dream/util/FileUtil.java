package i.dream.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 * Create Date Time: 2020-08-31 10:24.
 */
public class FileUtil {
//    private static final String filePath = "/opt/conf/raft.properties";
    private static final String filePath = "M:\\github\\raft-learning\\conf\\raft.properties";
    private static Properties properties = new Properties();

    // cluster_port = [server_port] + 10000
    private static int SERVER_PORT = 1001;
    private static int CLUSTER_SERVER_PORT = 10000 + SERVER_PORT;

    public static Properties readConfig() throws Exception {
        InputStream fileInput = null;
        try {
            String confPath = null == System.getProperty("conf") ? filePath : "";
            fileInput = new FileInputStream(new File(confPath));
            properties.load(fileInput);
        } catch (FileNotFoundException nx) {
            throw new Exception("file not exist, please specify the position of config file!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fileInput) {
                try {
                    fileInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return properties;
    }

    public static String getClusterIp() {
        if (null != properties) {
            return ((String) properties.get("bind")).split(":")[0];
        }

        return "localhost";
    }

    public static Integer getClusterServerPort() {
        if (null != properties) {
            String serverPort = (String)properties.get("port");
            return Integer.parseInt(serverPort) + 10000;
        }

        return CLUSTER_SERVER_PORT;
    }

    public static Integer getServerPort() {
        if (null != properties) {
            String serverPort = (String)properties.get("port");
            return Integer.parseInt(serverPort);
        }

        return SERVER_PORT;
    }
}
