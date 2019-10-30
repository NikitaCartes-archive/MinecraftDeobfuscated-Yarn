package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;

@Environment(EnvType.CLIENT)
public class RealmsUtil {
	private static final YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Realms.getProxy(), UUID.randomUUID().toString());
	private static final MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
	public static LoadingCache<String, GameProfile> gameProfileCache = CacheBuilder.newBuilder()
		.expireAfterWrite(60L, TimeUnit.MINUTES)
		.build(new CacheLoader<String, GameProfile>() {
			public GameProfile method_21571(String string) throws Exception {
				GameProfile gameProfile = RealmsUtil.sessionService.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), null), false);
				if (gameProfile == null) {
					throw new Exception("Couldn't get profile");
				} else {
					return gameProfile;
				}
			}
		});

	public static String uuidToName(String uuid) throws Exception {
		GameProfile gameProfile = gameProfileCache.get(uuid);
		return gameProfile.getName();
	}

	public static Map<Type, MinecraftProfileTexture> getTextures(String uuid) {
		try {
			GameProfile gameProfile = gameProfileCache.get(uuid);
			return sessionService.getTextures(gameProfile, false);
		} catch (Exception var2) {
			return Maps.<Type, MinecraftProfileTexture>newHashMap();
		}
	}

	public static void browseTo(String uri) {
		Realms.openUri(uri);
	}

	public static String convertToAgePresentation(Long timeDiff) {
		if (timeDiff < 0L) {
			return "right now";
		} else {
			long l = timeDiff / 1000L;
			if (l < 60L) {
				return (l == 1L ? "1 second" : l + " seconds") + " ago";
			} else if (l < 3600L) {
				long m = l / 60L;
				return (m == 1L ? "1 minute" : m + " minutes") + " ago";
			} else if (l < 86400L) {
				long m = l / 3600L;
				return (m == 1L ? "1 hour" : m + " hours") + " ago";
			} else {
				long m = l / 86400L;
				return (m == 1L ? "1 day" : m + " days") + " ago";
			}
		}
	}
}
