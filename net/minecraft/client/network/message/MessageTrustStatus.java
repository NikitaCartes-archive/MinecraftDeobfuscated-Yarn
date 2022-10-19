/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.message;

import java.time.Instant;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public enum MessageTrustStatus {
    SECURE,
    MODIFIED,
    NOT_SECURE;


    public static MessageTrustStatus getStatus(SignedMessage message, Text decorated, Instant receptionTimestamp) {
        if (!message.hasSignature() || message.isExpiredOnClient(receptionTimestamp)) {
            return NOT_SECURE;
        }
        if (MessageTrustStatus.isModified(message, decorated)) {
            return MODIFIED;
        }
        return SECURE;
    }

    private static boolean isModified(SignedMessage message, Text decorated) {
        if (!decorated.getString().contains(message.getSignedContent())) {
            return true;
        }
        Text text = message.unsignedContent();
        if (text == null) {
            return false;
        }
        return MessageTrustStatus.isNotInDefaultFont(text);
    }

    private static boolean isNotInDefaultFont(Text content) {
        return content.visit((style, part) -> {
            if (MessageTrustStatus.isNotInDefaultFont(style)) {
                return Optional.of(true);
            }
            return Optional.empty();
        }, Style.EMPTY).orElse(false);
    }

    private static boolean isNotInDefaultFont(Style style) {
        return !style.getFont().equals(Style.DEFAULT_FONT_ID);
    }

    public boolean isInsecure() {
        return this == NOT_SECURE;
    }

    @Nullable
    public MessageIndicator createIndicator(SignedMessage message) {
        return switch (this) {
            case MODIFIED -> MessageIndicator.modified(message.getSignedContent());
            case NOT_SECURE -> MessageIndicator.notSecure();
            default -> null;
        };
    }
}

