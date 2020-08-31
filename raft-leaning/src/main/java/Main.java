import i.dream.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        if (null == args || args.length < 1) {
            throw new IllegalArgumentException("");
        }

        Properties config = FileUtil.readConfig();
        String addressList = config.getProperty("bind");


        return;
    }

}
