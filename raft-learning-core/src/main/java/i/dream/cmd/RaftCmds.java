package i.dream.cmd;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public enum RaftCmds {

	OP_SET((byte) 0, SetCmd.class),
	OP_GET((byte) 1, SetCmd.class);

	private byte opCode;
	private Class<? extends Cmd> opClass;

	RaftCmds(byte opCode, Class<? extends Cmd> opClass) {
		this.opCode = opCode;
		this.opClass = opClass;
	}

	public byte getOpCode() {
		return opCode;
	}
}
