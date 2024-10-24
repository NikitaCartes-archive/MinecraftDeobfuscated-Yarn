package net.minecraft.client.network.message;

import com.mojang.serialization.Codec;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

@Environment(EnvType.CLIENT)
public enum MessageTrustStatus implements StringIdentifiable {
	SECURE("secure"),
	MODIFIED("modified"),
	NOT_SECURE("not_secure");

	public static final Codec<MessageTrustStatus> CODEC = StringIdentifiable.createCodec(MessageTrustStatus::values);
	private final String id;

	private MessageTrustStatus(final String id) {
		this.id = id;
	}

	public static MessageTrustStatus getStatus(SignedMessage message, Text decorated, Instant receptionTimestamp) {
		if (!message.hasSignature() || message.isExpiredOnClient(receptionTimestamp)) {
			return NOT_SECURE;
		} else {
			return isModified(message, decorated) ? MODIFIED : SECURE;
		}
	}

	private static boolean isModified(SignedMessage message, Text decorated) {
		if (!decorated.getString().contains(message.getSignedContent())) {
			return true;
		} else {
			Text text = message.unsignedContent();
			return text == null ? false : isNotInDefaultFont(text);
		}
	}

	private static boolean isNotInDefaultFont(Text content) {
		return (Boolean)content.visit((style, part) -> isNotInDefaultFont(style) ? Optional.of(true) : Optional.empty(), Style.EMPTY).orElse(false);
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

	@Override
	public String asString() {
		return this.id;
	}
}
