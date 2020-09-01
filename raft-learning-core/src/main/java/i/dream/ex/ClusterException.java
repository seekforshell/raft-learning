package i.dream.ex;

/**
 * Description:
 *
 * @author: yujingzhi
 * Version: 1.0
 * Create Date Time: 2020-09-01 11:08.
 */
public class ClusterException extends RuntimeException {

    public ClusterException(String message) {
        super(message);
    }

    public ClusterException(String message, Throwable cause) {
        super(message, cause);
    }

}
