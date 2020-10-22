package net.minecraft.client.realms.util;

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
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class RealmsUtil {
	private static final YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(MinecraftClient.getInstance().getNetworkProxy());
	private static final MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
	public static LoadingCache<String, GameProfile> gameProfileCache = CacheBuilder.newBuilder()
		.expireAfterWrite(60L, TimeUnit.MINUTES)
		.build(new CacheLoader<String, GameProfile>() {
			public GameProfile load(String string) throws Exception {
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

	public static String convertToAgePresentation(long l) {
		if (l < 0L) {
			return "right now";
		} else {
			long m = l / 1000L;
			if (m < 60L) {
				return (m == 1L ? "1 second" : m + " seconds") + " ago";
			} else if (m < 3600L) {
				long n = m / 60L;
				return (n == 1L ? "1 minute" : n + " minutes") + " ago";
			} else if (m < 86400L) {
				long n = m / 3600L;
				return (n == 1L ? "1 hour" : n + " hours") + " ago";
			} else {
				long n = m / 86400L;
				return (n == 1L ? "1 day" : n + " days") + " ago";
			}
		}
	}

	public static String method_25282(Date date) {
		return convertToAgePresentation(System.currentTimeMillis() - date.getTime());
	}
}
