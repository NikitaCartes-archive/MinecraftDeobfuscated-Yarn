/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.message;

import java.time.Instant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public enum MessageTrustStatus {
    SECURE,
    MODIFIED,
    NOT_SECURE;


    public static MessageTrustStatus getStatus(SignedMessage message, Text decorated, @Nullable PlayerListEntry sender, Instant receptionTimestamp) {
        if (message.isExpiredOnClient(receptionTimestamp)) {
            return NOT_SECURE;
        }
        if (sender == null || !sender.getMessageVerifier().verify(message)) {
            return NOT_SECURE;
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
        return this == NOT_SECURE;
    }

    @Nullable
    public MessageIndicator createIndicator(SignedMessage message) {
        return switch (this) {
            case MODIFIED -> MessageIndicator.modified(message.getSignedContent().plain());
            case NOT_SECURE -> MessageIndicator.notSecure();
            default -> null;
        };
    }
}

