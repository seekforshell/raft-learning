package i.dream.raft.log;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class LogManager {
	/**
	 * 所有的日志文件地址
	 */
	private Deque<Path> entryDeque = new ArrayDeque();

	/**
	 * 日志队列
	 */
	private Queue<LogEntry> logQueue = new ConcurrentLinkedQueue<>();

	/**
	 * 日志文件分隔大小
	 */
	private int LOG_SEGMENT = 10 * 1024 * 1024;

}
