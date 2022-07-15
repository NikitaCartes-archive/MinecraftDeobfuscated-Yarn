package net.minecraft.network.message;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.DynamicRegistryManager;

/**
 * A class wrapping {@link SignedMessage} on the server to allow custom behavior for
 * sending messages.
 */
public interface SentMessage {
	/**
	 * {@return the wrapped message}
	 */
	SignedMessage getWrappedMessage();

	/**
	 * {@return the chat message packet to be sent}
	 */
	ChatMessageS2CPacket toPacket(ServerPlayerEntity player, MessageType.Parameters params);

	/**
	 * Called after sending the message to applicable clients.
	 * 
	 * @apiNote This is used to send the message header to clients that didn't receive
	 * the message due to text filtering.
	 * 
	 * @see PlayerManager#sendMessageHeader
	 */
	void afterPacketsSent(PlayerManager playerManager);

	/**
	 * {@return the wrapped {@code message}}
	 * 
	 * @param profile the original source of the message
	 */
	static SentMessage of(SignedMessage message, MessageSourceProfile profile) {
		if (message.createMetadata().lacksSender()) {
			return new SentMessage.Profileless(message);
		} else {
			return (SentMessage)(!message.createMetadata().sender().equals(profile.profileId()) ? new SentMessage.Entity(message) : new SentMessage.Chat(message));
		}
	}

	/**
	 * {@return the wrapped {@code message}}
	 */
	static FilteredMessage<SentMessage> of(FilteredMessage<SignedMessage> message, MessageSourceProfile profile) {
		return message.mapParts(rawMessage -> of(message.raw(), profile), SentMessage.Profileless::new);
	}

	/**
	 * The wrapper used for normal chat messages.
	 * 
	 * <p>Text filtering can cause some players to not receive this kind of message.
	 * Message header is sent separately to those players.
	 */
	public static class Chat implements SentMessage {
		private final SignedMessage message;
		private final Set<ServerPlayerEntity> recipients = Sets.newIdentityHashSet();

		public Chat(SignedMessage message) {
			this.message = message;
		}

		@Override
		public SignedMessage getWrappedMessage() {
			return this.message;
		}

		@Override
		public ChatMessageS2CPacket toPacket(ServerPlayerEntity player, MessageType.Parameters params) {
			this.recipients.add(player);
			DynamicRegistryManager dynamicRegistryManager = player.world.getRegistryManager();
			MessageType.Serialized serialized = params.toSerialized(dynamicRegistryManager);
			return new ChatMessageS2CPacket(this.message, serialized);
		}

		@Override
		public void afterPacketsSent(PlayerManager playerManager) {
			playerManager.sendMessageHeader(this.message, this.recipients);
		}
	}

	/**
	 * The wrapper used for messages sent from entities via {@code /execute} command.
	 * 
	 * <p>The client receives the message without the metadata first; the header is sent
	 * separately.
	 */
	public static class Entity implements SentMessage {
		private final SignedMessage originalMessage;
		private final SignedMessage messageWithoutMetadata;

		public Entity(SignedMessage originalMessage) {
			this.originalMessage = originalMessage;
			this.messageWithoutMetadata = SignedMessage.ofUnsigned(MessageMetadata.of(), originalMessage.getContent());
		}

		@Override
		public SignedMessage getWrappedMessage() {
			return this.originalMessage;
		}

		@Override
		public ChatMessageS2CPacket toPacket(ServerPlayerEntity player, MessageType.Parameters params) {
			DynamicRegistryManager dynamicRegistryManager = player.world.getRegistryManager();
			MessageType.Serialized serialized = params.toSerialized(dynamicRegistryManager);
			return new ChatMessageS2CPacket(this.messageWithoutMetadata, serialized);
		}

		@Override
		public void afterPacketsSent(PlayerManager playerManager) {
			playerManager.sendMessageHeader(this.originalMessage, Set.of());
		}
	}

	/**
	 * The wrapper used for messages without associated source profile.
	 */
	public static class Profileless implements SentMessage {
		private final SignedMessage message;

		public Profileless(SignedMessage message) {
			this.message = message;
		}

		@Override
		public SignedMessage getWrappedMessage() {
			return this.message;
		}

		@Override
		public ChatMessageS2CPacket toPacket(ServerPlayerEntity player, MessageType.Parameters params) {
			DynamicRegistryManager dynamicRegistryManager = player.world.getRegistryManager();
			MessageType.Serialized serialized = params.toSerialized(dynamicRegistryManager);
			return new ChatMessageS2CPacket(this.message, serialized);
		}

		@Override
		public void afterPacketsSent(PlayerManager playerManager) {
		}
	}
}
