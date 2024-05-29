package net.minecraft.client.session;

import com.mojang.authlib.minecraft.BanDetails;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class Bans {
	private static final Text TEMPORARY_TITLE = Text.translatable("gui.banned.title.temporary").formatted(Formatting.BOLD);
	private static final Text PERMANENT_TITLE = Text.translatable("gui.banned.title.permanent").formatted(Formatting.BOLD);
	public static final Text NAME_TITLE = Text.translatable("gui.banned.name.title").formatted(Formatting.BOLD);
	private static final Text SKIN_TITLE = Text.translatable("gui.banned.skin.title").formatted(Formatting.BOLD);
	private static final Text SKIN_DESCRIPTION = Text.translatable("gui.banned.skin.description", Text.of(Urls.JAVA_MODERATION));

	public static ConfirmLinkScreen createBanScreen(BooleanConsumer callback, BanDetails banDetails) {
		return new ConfirmLinkScreen(callback, getTitle(banDetails), getDescriptionText(banDetails), Urls.JAVA_MODERATION, ScreenTexts.ACKNOWLEDGE, true);
	}

	public static ConfirmLinkScreen createSkinBanScreen(Runnable onClose) {
		URI uRI = Urls.JAVA_MODERATION;
		return new ConfirmLinkScreen(confirmed -> {
			if (confirmed) {
				Util.getOperatingSystem().open(uRI);
			}

			onClose.run();
		}, SKIN_TITLE, SKIN_DESCRIPTION, uRI, ScreenTexts.ACKNOWLEDGE, true);
	}

	public static ConfirmLinkScreen createUsernameBanScreen(String username, Runnable onClose) {
		URI uRI = Urls.JAVA_MODERATION;
		return new ConfirmLinkScreen(
			confirmed -> {
				if (confirmed) {
					Util.getOperatingSystem().open(uRI);
				}

				onClose.run();
			},
			NAME_TITLE,
			Text.translatable("gui.banned.name.description", Text.literal(username).formatted(Formatting.YELLOW), Text.of(Urls.JAVA_MODERATION)),
			uRI,
			ScreenTexts.ACKNOWLEDGE,
			true
		);
	}

	private static Text getTitle(BanDetails banDetails) {
		return isTemporary(banDetails) ? TEMPORARY_TITLE : PERMANENT_TITLE;
	}

	private static Text getDescriptionText(BanDetails banDetails) {
		return Text.translatable("gui.banned.description", getReasonText(banDetails), getDurationText(banDetails), Text.of(Urls.JAVA_MODERATION));
	}

	private static Text getReasonText(BanDetails banDetails) {
		String string = banDetails.reason();
		String string2 = banDetails.reasonMessage();
		if (StringUtils.isNumeric(string)) {
			int i = Integer.parseInt(string);
			BanReason banReason = BanReason.byId(i);
			Text text;
			if (banReason != null) {
				text = Texts.setStyleIfAbsent(banReason.getDescription().copy(), Style.EMPTY.withBold(true));
			} else if (string2 != null) {
				text = Text.translatable("gui.banned.description.reason_id_message", i, string2).formatted(Formatting.BOLD);
			} else {
				text = Text.translatable("gui.banned.description.reason_id", i).formatted(Formatting.BOLD);
			}

			return Text.translatable("gui.banned.description.reason", text);
		} else {
			return Text.translatable("gui.banned.description.unknownreason");
		}
	}

	private static Text getDurationText(BanDetails banDetails) {
		if (isTemporary(banDetails)) {
			Text text = getTemporaryBanDurationText(banDetails);
			return Text.translatable("gui.banned.description.temporary", Text.translatable("gui.banned.description.temporary.duration", text).formatted(Formatting.BOLD));
		} else {
			return Text.translatable("gui.banned.description.permanent").formatted(Formatting.BOLD);
		}
	}

	private static Text getTemporaryBanDurationText(BanDetails banDetails) {
		Duration duration = Duration.between(Instant.now(), banDetails.expires());
		long l = duration.toHours();
		if (l > 72L) {
			return ScreenTexts.days(duration.toDays());
		} else {
			return l < 1L ? ScreenTexts.minutes(duration.toMinutes()) : ScreenTexts.hours(duration.toHours());
		}
	}

	private static boolean isTemporary(BanDetails banDetails) {
		return banDetails.expires() != null;
	}
}
