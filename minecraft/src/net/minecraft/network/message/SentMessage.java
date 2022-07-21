package net.minecraft.network.message;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.DynamicRegistryManager;

/**
 * A class wrapping {@link SignedMessage} on the server to allow custom behavior for
 * sending messages.
 */
public interface SentMessage {
	Text method_45039();

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
	 */
	static SentMessage of(SignedMessage message) {
		return (SentMessage)(message.createMetadata().lacksSender() ? new SentMessage.Profileless(message) : new SentMessage.Chat(message));
	}

	/**
	 * {@return the wrapped {@code message}}
	 */
	static FilteredMessage<SentMessage> of(FilteredMessage<SignedMessage> message) {
		SentMessage sentMessage = of(message.raw());
		return message.method_45000(sentMessage, SentMessage.Profileless::new);
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
		public Text method_45039() {
			return this.message.getContent();
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
	 * The wrapper used for messages without associated source profile.
	 */
	public static class Profileless implements SentMessage {
		private final SignedMessage message;

		public Profileless(SignedMessage message) {
			this.message = message;
		}

		@Override
		public Text method_45039() {
			return this.message.getContent();
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
