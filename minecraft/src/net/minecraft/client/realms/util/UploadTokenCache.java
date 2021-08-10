package net.minecraft.client.realms.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UploadTokenCache {
	private static final Long2ObjectMap<String> TOKEN_CACHE = new Long2ObjectOpenHashMap<>();

	public static String get(long worldId) {
		return TOKEN_CACHE.get(worldId);
	}

	public static void invalidate(long world) {
		TOKEN_CACHE.remove(world);
	}

	public static void put(long wid, String token) {
		TOKEN_CACHE.put(wid, token);
	}
}
