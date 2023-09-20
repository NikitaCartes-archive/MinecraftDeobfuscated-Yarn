package net.minecraft.client.realms.util;

import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.Date;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RealmsUtil {
	private static final Text NOW_TEXT = Text.translatable("mco.util.time.now");
	private static final int SECONDS_PER_MINUTE = 60;
	private static final int SECONDS_PER_HOUR = 3600;
	private static final int SECONDS_PER_DAY = 86400;

	public static Text convertToAgePresentation(long milliseconds) {
		if (milliseconds < 0L) {
			return NOW_TEXT;
		} else {
			long l = milliseconds / 1000L;
			if (l < 60L) {
				return Text.translatable("mco.time.secondsAgo", l);
			} else if (l < 3600L) {
				long m = l / 60L;
				return Text.translatable("mco.time.minutesAgo", m);
			} else if (l < 86400L) {
				long m = l / 3600L;
				return Text.translatable("mco.time.hoursAgo", m);
			} else {
				long m = l / 86400L;
				return Text.translatable("mco.time.daysAgo", m);
			}
		}
	}

	public static Text convertToAgePresentation(Date date) {
		return convertToAgePresentation(System.currentTimeMillis() - date.getTime());
	}

	public static void drawPlayerHead(DrawContext context, int x, int y, int size, UUID playerUuid) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		ProfileResult profileResult = minecraftClient.getSessionService().fetchProfile(playerUuid, false);
		SkinTextures skinTextures = profileResult != null
			? minecraftClient.getSkinProvider().getSkinTextures(profileResult.profile())
			: DefaultSkinHelper.getSkinTextures(playerUuid);
		PlayerSkinDrawer.draw(context, skinTextures.texture(), x, y, size);
	}
}
