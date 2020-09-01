package i.dream.raft.cluster;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 * Create Date Time: 2020-09-01 10:23.
 */
public class ClusterBootStrap {

    public boolean start() {
        boolean success = true;

        ClusterServer bootStrap = new ClusterServer();
        try {
            bootStrap.init();
        } catch (Exception e) {
            success = false;
        }

        return success;
    }
}
