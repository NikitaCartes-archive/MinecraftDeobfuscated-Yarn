package net.minecraft.client.realms.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RealmsUtil {
	static final MinecraftSessionService SESSION_SERVICE = MinecraftClient.getInstance().getSessionService();
	private static final LoadingCache<String, GameProfile> gameProfileCache = CacheBuilder.newBuilder()
		.expireAfterWrite(60L, TimeUnit.MINUTES)
		.build(new CacheLoader<String, GameProfile>() {
			public GameProfile load(String string) {
				return RealmsUtil.SESSION_SERVICE.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), null), false);
			}
		});
	private static final int SECONDS_PER_MINUTE = 60;
	private static final int SECONDS_PER_HOUR = 3600;
	private static final int SECONDS_PER_DAY = 86400;

	public static String uuidToName(String uuid) {
		return gameProfileCache.getUnchecked(uuid).getName();
	}

	public static GameProfile uuidToProfile(String uuid) {
		return gameProfileCache.getUnchecked(uuid);
	}

	public static String convertToAgePresentation(long milliseconds) {
		if (milliseconds < 0L) {
			return "right now";
		} else {
			long l = milliseconds / 1000L;
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

	public static String convertToAgePresentation(Date date) {
		return convertToAgePresentation(System.currentTimeMillis() - date.getTime());
	}

	public static void drawPlayerHead(DrawContext context, int x, int y, int size, String uuid) {
		GameProfile gameProfile = uuidToProfile(uuid);
		Identifier identifier = MinecraftClient.getInstance().getSkinProvider().loadSkin(gameProfile);
		PlayerSkinDrawer.draw(context, identifier, x, y, size);
	}
}
