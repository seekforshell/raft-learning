package i.dream.raft.log;

import i.dream.cmd.Cmd;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
@Data
public class LogEntry {

	private int logIdx;

	private int logTerm;

	private Cmd cmd;

	byte[] write() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutput dataOutput = new DataOutputStream(out);
		dataOutput.writeInt(logIdx);
		dataOutput.writeInt(logTerm);
		dataOutput.write(cmd.write());

		return out.toByteArray();
	}
}
