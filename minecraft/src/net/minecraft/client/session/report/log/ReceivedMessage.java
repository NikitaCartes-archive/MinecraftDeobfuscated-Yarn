package net.minecraft.client.session.report.log;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;

/**
 * A message received by the client and stored in {@link ChatLog}.
 * 
 * <p>This includes both {@linkplain net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket
 * chat messages} and {@linkplain net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
 * game messages}.
 */
@Environment(EnvType.CLIENT)
public interface ReceivedMessage extends ChatLogEntry {
	/**
	 * {@return the received message constructed from a chat message}
	 * 
	 * @param gameProfile the game profile of the message's sender
	 */
	static ReceivedMessage.ChatMessage of(GameProfile gameProfile, SignedMessage message, MessageTrustStatus trustStatus) {
		return new ReceivedMessage.ChatMessage(gameProfile, message, trustStatus);
	}

	/**
	 * {@return the received message constructed from a game message's elements}
	 * 
	 * @param message the message content
	 * @param timestamp the timestamp of the message
	 */
	static ReceivedMessage.GameMessage of(Text message, Instant timestamp) {
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
	public static record ChatMessage(GameProfile profile, SignedMessage message, MessageTrustStatus trustStatus) implements ReceivedMessage {
		public static final MapCodec<ReceivedMessage.ChatMessage> CHAT_MESSAGE_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codecs.GAME_PROFILE_WITH_PROPERTIES.fieldOf("profile").forGetter(ReceivedMessage.ChatMessage::profile),
						SignedMessage.CODEC.forGetter(ReceivedMessage.ChatMessage::message),
						MessageTrustStatus.CODEC.optionalFieldOf("trust_level", MessageTrustStatus.SECURE).forGetter(ReceivedMessage.ChatMessage::trustStatus)
					)
					.apply(instance, ReceivedMessage.ChatMessage::new)
		);
		private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

		@Override
		public Text getContent() {
			if (!this.message.filterMask().isPassThrough()) {
				Text text = this.message.filterMask().getFilteredText(this.message.getSignedContent());
				return (Text)(text != null ? text : Text.empty());
			} else {
				return this.message.getContent();
			}
		}

		@Override
		public Text getNarration() {
			Text text = this.getContent();
			Text text2 = this.getFormattedTimestamp();
			return Text.translatable("gui.chatSelection.message.narrate", this.profile.getName(), text, text2);
		}

		/**
		 * {@return the heading text used by Chat Selection screen}
		 * 
		 * <p>The text contains the sender's display name and the formatted timestamp.
		 */
		public Text getHeadingText() {
			Text text = this.getFormattedTimestamp();
			return Text.translatable("gui.chatSelection.heading", this.profile.getName(), text);
		}

		/**
		 * {@return the formatted timestamp text of this message}
		 */
		private Text getFormattedTimestamp() {
			LocalDateTime localDateTime = LocalDateTime.ofInstant(this.message.getTimestamp(), ZoneOffset.systemDefault());
			return Text.literal(localDateTime.format(DATE_TIME_FORMATTER)).formatted(Formatting.ITALIC, Formatting.GRAY);
		}

		@Override
		public boolean isSentFrom(UUID uuid) {
			return this.message.canVerifyFrom(uuid);
		}

		/**
		 * {@return the UUID of the sender}
		 */
		public UUID getSenderUuid() {
			return this.profile.getId();
		}

		@Override
		public ChatLogEntry.Type getType() {
			return ChatLogEntry.Type.PLAYER;
		}
	}

	/**
	 * A game message received by the client.
	 */
	@Environment(EnvType.CLIENT)
	public static record GameMessage(Text message, Instant timestamp) implements ReceivedMessage {
		public static final MapCodec<ReceivedMessage.GameMessage> GAME_MESSAGE_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						TextCodecs.CODEC.fieldOf("message").forGetter(ReceivedMessage.GameMessage::message),
						Codecs.INSTANT.fieldOf("time_stamp").forGetter(ReceivedMessage.GameMessage::timestamp)
					)
					.apply(instance, ReceivedMessage.GameMessage::new)
		);

		@Override
		public Text getContent() {
			return this.message;
		}

		@Override
		public boolean isSentFrom(UUID uuid) {
			return false;
		}

		@Override
		public ChatLogEntry.Type getType() {
			return ChatLogEntry.Type.SYSTEM;
		}
	}
}
