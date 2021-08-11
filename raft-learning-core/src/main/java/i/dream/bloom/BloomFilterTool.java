package i.dream.bloom;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * @author: yujingzhi
 * Version: 1.0
 */
public class BloomFilterTool {
	private static final BloomFilter<String> keyFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 10000, 0.001);

	public void addKey(String key) {
		if (!Strings.isNullOrEmpty(key)) {
			keyFilter.put(key);
		} else {
			throw new IllegalArgumentException("key is empty");
		}
	}

	public boolean containKey(String key) {
		return keyFilter.mightContain(key);
	}
}
