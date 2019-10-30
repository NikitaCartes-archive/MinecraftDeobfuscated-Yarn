package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UploadTokenCache {
	private static final Map<Long, String> tokenCache = Maps.<Long, String>newHashMap();

	public static String get(long worldId) {
		return (String)tokenCache.get(worldId);
	}

	public static void invalidate(long world) {
		tokenCache.remove(world);
	}

	public static void put(long wid, String token) {
		tokenCache.put(wid, token);
	}
}
