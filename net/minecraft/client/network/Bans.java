/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import com.mojang.authlib.minecraft.BanDetails;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.time.Duration;
import java.time.Instant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.report.AbuseReportReason;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class Bans {
    public static final String JAVA_MODERATION_URL = "https://aka.ms/mcjavamoderation";
    private static final Text TEMPORARY_TITLE = Text.translatable("gui.banned.title.temporary").formatted(Formatting.BOLD);
    private static final Text PERMANENT_TITLE = Text.translatable("gui.banned.title.permanent").formatted(Formatting.BOLD);

    public static ConfirmLinkScreen createBanScreen(BooleanConsumer callback, BanDetails banDetails) {
        return new ConfirmLinkScreen(callback, Bans.getTitle(banDetails), Bans.getDescriptionText(banDetails), JAVA_MODERATION_URL, ScreenTexts.ACKNOWLEDGE, true);
    }

    private static Text getTitle(BanDetails banDetails) {
        return Bans.isTemporary(banDetails) ? TEMPORARY_TITLE : PERMANENT_TITLE;
    }

    private static Text getDescriptionText(BanDetails banDetails) {
        return Text.translatable("gui.banned.description", Bans.getReasonText(banDetails), Bans.getDurationText(banDetails), Text.literal(JAVA_MODERATION_URL));
    }

    private static Text getReasonText(BanDetails banDetails) {
        String string = banDetails.reason();
        String string2 = banDetails.reasonMessage();
        if (StringUtils.isNumeric(string)) {
            int i = Integer.parseInt(string);
            Text text = AbuseReportReason.getText(i);
            text = text != null ? Texts.setStyleIfAbsent(text.copy(), Style.EMPTY.withBold(true)) : (string2 != null ? Text.translatable("gui.banned.description.reason_id_message", i, string2).formatted(Formatting.BOLD) : Text.translatable("gui.banned.description.reason_id", i).formatted(Formatting.BOLD));
            return Text.translatable("gui.banned.description.reason", text);
        }
        return Text.translatable("gui.banned.description.unknownreason");
    }

    private static Text getDurationText(BanDetails banDetails) {
        if (Bans.isTemporary(banDetails)) {
            Text text = Bans.getTemporaryBanDurationText(banDetails);
            return Text.translatable("gui.banned.description.temporary", Text.translatable("gui.banned.description.temporary.duration", text).formatted(Formatting.BOLD));
        }
        return Text.translatable("gui.banned.description.permanent").formatted(Formatting.BOLD);
    }

    private static Text getTemporaryBanDurationText(BanDetails banDetails) {
        Duration duration = Duration.between(Instant.now(), banDetails.expires());
        long l = duration.toHours();
        if (l > 72L) {
            return ScreenTexts.days(duration.toDays());
        }
        if (l < 1L) {
            return ScreenTexts.minutes(duration.toMinutes());
        }
        return ScreenTexts.hours(duration.toHours());
    }

    private static boolean isTemporary(BanDetails banDetails) {
        return banDetails.expires() != null;
    }
}

