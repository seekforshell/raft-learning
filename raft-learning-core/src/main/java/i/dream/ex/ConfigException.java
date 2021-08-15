package i.dream.ex;

import java.io.IOException;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class ConfigException extends IOException {

	public ConfigException(String message, String... arg) {
		super(String.format(message, arg));
	}
}
