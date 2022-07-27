package net.minecraft.client.network.message;

import java.time.Instant;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.message.MessageVerifier;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public enum MessageTrustStatus {
	SECURE,
	MODIFIED,
	FILTERED,
	NOT_SECURE,
	BROKEN_CHAIN;

	public static MessageTrustStatus getStatus(SignedMessage message, Text decorated, @Nullable PlayerListEntry sender, Instant receptionTimestamp) {
		if (sender == null) {
			return NOT_SECURE;
		} else {
			MessageVerifier.Status status = sender.getMessageVerifier().verify(message);
			if (status == MessageVerifier.Status.BROKEN_CHAIN) {
				return BROKEN_CHAIN;
			} else if (status == MessageVerifier.Status.NOT_SECURE) {
				return NOT_SECURE;
			} else if (message.isExpiredOnClient(receptionTimestamp)) {
				return NOT_SECURE;
			} else if (!message.filterMask().isPassThrough()) {
				return FILTERED;
			} else if (message.unsignedContent().isPresent()) {
				return MODIFIED;
			} else {
				return !decorated.contains(message.getSignedContent().decorated()) ? MODIFIED : SECURE;
			}
		}
	}

	public boolean isInsecure() {
		return this == NOT_SECURE || this == BROKEN_CHAIN;
	}

	@Nullable
	public MessageIndicator createIndicator(SignedMessage message) {
		return switch (this) {
			case MODIFIED -> MessageIndicator.modified(message.getSignedContent().plain());
			case FILTERED -> MessageIndicator.filtered();
			case NOT_SECURE -> MessageIndicator.notSecure();
			default -> null;
		};
	}
}
