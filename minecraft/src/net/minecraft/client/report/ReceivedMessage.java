package net.minecraft.client.report;

import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * A message received by the client and stored in {@link ChatLog}.
 * 
 * <p>This includes both {@linkplain net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket
 * chat messages} and {@linkplain net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
 * game messages}.
 */
@Environment(EnvType.CLIENT)
public interface ReceivedMessage {
	/**
	 * {@return the received message constructed from a chat message's elements}
	 * 
	 * @param gameProfile the game profile of the message's sender
	 * @param displayName the displayed name of the sender
	 * @param message the message content
	 */
	static ReceivedMessage of(GameProfile gameProfile, Text displayName, SignedMessage message) {
		return new ReceivedMessage.ChatMessage(gameProfile, displayName, message);
	}

	/**
	 * {@return the received message constructed from a game message's elements}
	 * 
	 * @param message the message content
	 * @param timestamp the timestamp of the message
	 */
	static ReceivedMessage of(Text message, Instant timestamp) {
		return new ReceivedMessage.GameMessage(message, timestamp);
	}

	/**
	 * {@return the content of the message}
	 * 
	 * @implNote If the message is a chat message and it contains an unsigned part, the unsigned
	 * part will be returned. Note that in vanilla, unsigned part is stripped prior to
	 * construction of the received message instance if the client requires secure chat.
	 */
	Text getContent();

	/**
	 * {@return the narration of the message (by default, the content)}
	 */
	default Text getNarration() {
		return this.getContent();
	}

	/**
	 * {@return whether the sender's UUID equals {@code uuid}}
	 */
	boolean isSentFrom(UUID uuid);

	/**
	 * A chat message received by the client.
	 */
	@Environment(EnvType.CLIENT)
	public static record ChatMessage(GameProfile profile, Text displayName, SignedMessage message) implements ReceivedMessage {
		private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

		@Override
		public Text getContent() {
			return this.message.getContent();
		}

		@Override
		public Text getNarration() {
			Text text = this.message.getContent();
			Text text2 = this.getFormattedTimestamp();
			return Text.translatable("gui.chatSelection.message.narrate", this.displayName, text, text2);
		}

		/**
		 * {@return the heading text used by Chat Selection screen}
		 * 
		 * <p>The text contains the sender's display name and the formatted timestamp.
		 */
		public Text getHeadingText() {
			Text text = this.getFormattedTimestamp();
			return Text.translatable("gui.chatSelection.heading", this.displayName, text);
		}

		/**
		 * {@return the formatted timestamp text of this message}
		 */
		private Text getFormattedTimestamp() {
			LocalDateTime localDateTime = LocalDateTime.ofInstant(this.message.signature().timestamp(), ZoneOffset.systemDefault());
			return Text.literal(localDateTime.format(DATE_TIME_FORMATTER)).formatted(Formatting.ITALIC, Formatting.GRAY);
		}

		@Override
		public boolean isSentFrom(UUID uuid) {
			return this.getSenderUuid().equals(uuid);
		}

		/**
		 * {@return the UUID of the sender}
		 */
		public UUID getSenderUuid() {
			return this.profile.getId();
		}
	}

	/**
	 * A game message received by the client.
	 */
	@Environment(EnvType.CLIENT)
	public static record GameMessage(Text message, Instant timestamp) implements ReceivedMessage {
		@Override
		public Text getContent() {
			return this.message;
		}

		@Override
		public boolean isSentFrom(UUID uuid) {
			return false;
		}
	}

	/**
	 * A pair of the message's index and the message itself.
	 * 
	 * @see ChatLog
	 */
	@Environment(EnvType.CLIENT)
	public static record IndexedMessage(int index, ReceivedMessage message) {
	}
}
