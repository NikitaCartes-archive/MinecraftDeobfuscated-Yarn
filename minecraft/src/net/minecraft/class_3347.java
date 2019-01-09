package net.minecraft;

import java.nio.charset.StandardCharsets;

public class class_3347 {
	public static final char[] field_14398 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	public static String method_14697(byte[] bs, int i, int j) {
		int k = j - 1;
		int l = i > k ? k : i;

		while (0 != bs[l] && l < k) {
			l++;
		}

		return new String(bs, i, l - i, StandardCharsets.UTF_8);
	}

	public static int method_14695(byte[] bs, int i) {
		return method_14696(bs, i, bs.length);
	}

	public static int method_14696(byte[] bs, int i, int j) {
		return 0 > j - i - 4 ? 0 : bs[i + 3] << 24 | (bs[i + 2] & 0xFF) << 16 | (bs[i + 1] & 0xFF) << 8 | bs[i] & 0xFF;
	}

	public static int method_14698(byte[] bs, int i, int j) {
		return 0 > j - i - 4 ? 0 : bs[i] << 24 | (bs[i + 1] & 0xFF) << 16 | (bs[i + 2] & 0xFF) << 8 | bs[i + 3] & 0xFF;
	}

	public static String method_14699(byte b) {
		return "" + field_14398[(b & 240) >>> 4] + field_14398[b & 15];
	}
}
