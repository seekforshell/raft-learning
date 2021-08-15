package i.dream.raft;

import java.nio.ByteBuffer;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public abstract class Types {
	/**
	 * Write the typed object to the buffer
	 *
	 */
	public abstract ByteBuffer write(ByteBuffer buffer);

	/**
	 * Read the typed object from the buffer
	 *
	 */
	public abstract Object read(ByteBuffer buffer);
}
