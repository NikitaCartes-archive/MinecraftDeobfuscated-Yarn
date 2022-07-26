/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.message;

import java.time.Instant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.message.MessageVerifier;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public enum MessageTrustStatus {
    SECURE,
    MODIFIED,
    FILTERED,
    NOT_SECURE,
    BROKEN_CHAIN;


    public static MessageTrustStatus getStatus(SignedMessage message, Text decorated, @Nullable PlayerListEntry sender, Instant receptionTimestamp) {
        if (sender == null) {
            return NOT_SECURE;
        }
        MessageVerifier.Status status = sender.getMessageVerifier().verify(message);
        if (status == MessageVerifier.Status.BROKEN_CHAIN) {
            return BROKEN_CHAIN;
        }
        if (status == MessageVerifier.Status.NOT_SECURE) {
            return NOT_SECURE;
        }
        if (message.isExpiredOnClient(receptionTimestamp)) {
            return NOT_SECURE;
        }
        if (!message.filterMask().method_45087()) {
            return FILTERED;
        }
        if (message.unsignedContent().isPresent()) {
            return MODIFIED;
        }
        if (!decorated.contains(message.getSignedContent().decorated())) {
            return MODIFIED;
        }
        return SECURE;
    }

    public boolean isInsecure() {
        return this == NOT_SECURE || this == BROKEN_CHAIN;
    }

    @Nullable
    public MessageIndicator createIndicator(SignedMessage message) {
        return switch (this) {
            case MODIFIED -> MessageIndicator.modified(message.getSignedContent().plain());
            case FILTERED -> MessageIndicator.method_45071();
            case NOT_SECURE -> MessageIndicator.notSecure();
            default -> null;
        };
    }
}

