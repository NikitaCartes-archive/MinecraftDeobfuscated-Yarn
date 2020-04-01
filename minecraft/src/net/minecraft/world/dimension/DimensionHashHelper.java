package net.minecraft.world.dimension;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class DimensionHashHelper {
	private static String lastSearchedName = "";

	public static int getHash(String name) {
		lastSearchedName = name;
		return Hashing.sha256().hashString(name + ":why_so_salty#LazyCrypto", StandardCharsets.UTF_8).asInt() & 2147483647;
	}

	public static String getLastSearchedName() {
		return lastSearchedName;
	}
}
