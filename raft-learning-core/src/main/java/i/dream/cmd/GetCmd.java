package i.dream.cmd;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class GetCmd extends Cmd {

	private final String cmdName = "get";

	private int key;

	GetCmd(int key, int value) {
		this.key = key;
	}

	public String getCmdName() {
		return cmdName;
	}

	@Override
	public byte[] write() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutput dataOutput = new DataOutputStream(out);
		dataOutput.write(new byte[] {RaftCmds.OP_SET.getOpCode()});
		dataOutput.writeInt(key);
		dataOutput.writeChars(LOG_SPLITOR);
		return ByteBuffer.wrap(out.toByteArray());
	}
}
