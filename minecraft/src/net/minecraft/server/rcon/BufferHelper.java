package net.minecraft.server.rcon;

import java.nio.charset.StandardCharsets;

public class BufferHelper {
	public static final char[] HEX_CHARS_LOOKUP = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	public static String getString(byte[] bs, int i, int j) {
		int k = j - 1;
		int l = i > k ? k : i;

		while (0 != bs[l] && l < k) {
			l++;
		}

		return new String(bs, i, l - i, StandardCharsets.UTF_8);
	}

	public static int getIntLE(byte[] bs, int i) {
		return getIntLE(bs, i, bs.length);
	}

	public static int getIntLE(byte[] bs, int i, int j) {
		return 0 > j - i - 4 ? 0 : bs[i + 3] << 24 | (bs[i + 2] & 0xFF) << 16 | (bs[i + 1] & 0xFF) << 8 | bs[i] & 0xFF;
	}

	public static int getIntBE(byte[] bs, int i, int j) {
		return 0 > j - i - 4 ? 0 : bs[i] << 24 | (bs[i + 1] & 0xFF) << 16 | (bs[i + 2] & 0xFF) << 8 | bs[i + 3] & 0xFF;
	}

	public static String toHex(byte b) {
		return "" + HEX_CHARS_LOOKUP[(b & 240) >>> 4] + HEX_CHARS_LOOKUP[b & 15];
	}
}
