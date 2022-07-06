package net.minecraft.client.network.message;

import java.time.Instant;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public enum MessageTrustStatus {
	SECURE,
	MODIFIED,
	NOT_SECURE;

	public static MessageTrustStatus getStatus(SignedMessage message, Text decorated, @Nullable PlayerListEntry sender) {
		if (message.isExpiredOnClient(Instant.now())) {
			return NOT_SECURE;
		} else {
			PlayerPublicKey playerPublicKey = Util.map(sender, PlayerListEntry::getPublicKeyData);
			if (playerPublicKey == null || !message.verify(playerPublicKey)) {
				return NOT_SECURE;
			} else if (message.unsignedContent().isPresent()) {
				return MODIFIED;
			} else {
				return !decorated.contains(message.signedContent()) ? MODIFIED : SECURE;
			}
		}
	}

	public boolean isInsecure() {
		return this == NOT_SECURE;
	}

	@Nullable
	public MessageIndicator createIndicator(SignedMessage message) {
		return switch (this) {
			case MODIFIED -> MessageIndicator.modified(message.signedContent());
			case NOT_SECURE -> MessageIndicator.notSecure();
			default -> null;
		};
	}
}
