package net.minecraft;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;

@Environment(EnvType.CLIENT)
public class class_4448 {
	private static final YggdrasilAuthenticationService field_20261 = new YggdrasilAuthenticationService(Realms.getProxy(), UUID.randomUUID().toString());
	private static final MinecraftSessionService field_20262 = field_20261.createMinecraftSessionService();
	public static LoadingCache<String, GameProfile> field_20260 = CacheBuilder.newBuilder()
		.expireAfterWrite(60L, TimeUnit.MINUTES)
		.build(new CacheLoader<String, GameProfile>() {
			public GameProfile method_21571(String string) throws Exception {
				GameProfile gameProfile = class_4448.field_20262.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), null), false);
				if (gameProfile == null) {
					throw new Exception("Couldn't get profile");
				} else {
					return gameProfile;
				}
			}
		});

	public static String method_21568(String string) throws Exception {
		GameProfile gameProfile = field_20260.get(string);
		return gameProfile.getName();
	}

	public static Map<Type, MinecraftProfileTexture> method_21569(String string) {
		try {
			GameProfile gameProfile = field_20260.get(string);
			return field_20262.getTextures(gameProfile, false);
		} catch (Exception var2) {
			return new HashMap();
		}
	}

	public static void method_21570(String string) {
		Realms.openUri(string);
	}

	public static String method_21567(Long long_) {
		if (long_ < 0L) {
			return "right now";
		} else {
			long l = long_ / 1000L;
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
