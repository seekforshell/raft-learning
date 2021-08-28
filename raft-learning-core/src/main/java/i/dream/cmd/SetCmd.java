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
public class SetCmd extends Cmd {

	private final String cmdName = "set";

	private int key;

	private int value;

	SetCmd(int key, int value) {
		this.key = key;
		this.value = value;
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
		dataOutput.writeInt(value);
		dataOutput.writeChars(LOG_SPLITOR);
		return ByteBuffer.wrap(out.toByteArray());
	}
}
