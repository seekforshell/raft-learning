package i.dtream.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandDispatcher {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void exec() {
        try {
            BufferedReader br =
                new BufferedReader(new InputStreamReader(System.in));

            String line;
            while ((line = br.readLine()) != null) {
                executeLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeLine(String line) {
        if (null == line || line.isEmpty()) {
            return;
        }

        if (line.length() > 1000) {
            System.out.println("command line is to long,  it's length must be less than 1000");
        }
    }
}
