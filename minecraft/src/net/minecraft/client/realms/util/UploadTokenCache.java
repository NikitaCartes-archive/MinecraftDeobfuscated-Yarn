package net.minecraft.client.realms.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UploadTokenCache {
	private static final Long2ObjectMap<String> tokenCache = new Long2ObjectOpenHashMap<>();

	public static String get(long worldId) {
		return tokenCache.get(worldId);
	}

	public static void invalidate(long world) {
		tokenCache.remove(world);
	}

	public static void put(long wid, String token) {
		tokenCache.put(wid, token);
	}
}
