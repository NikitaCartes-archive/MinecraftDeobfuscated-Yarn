package net.minecraft.network.message;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * A class wrapping {@link SignedMessage} on the server to allow custom behavior for
 * sending messages.
 */
public interface SentMessage {
	Text getContent();

	void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params);

	/**
	 * {@return the wrapped {@code message}}
	 */
	static SentMessage of(SignedMessage message) {
		return (SentMessage)(message.isSenderMissing() ? new SentMessage.Profileless(message.getContent()) : new SentMessage.Chat(message));
	}

	/**
	 * The wrapper used for normal chat messages.
	 * 
	 * <p>Text filtering can cause some players to not receive this kind of message.
	 */
	public static record Chat(SignedMessage message) implements SentMessage {
		@Override
		public Text getContent() {
			return this.message.getContent();
		}

		@Override
		public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
			SignedMessage signedMessage = this.message.withFilterMaskEnabled(filterMaskEnabled);
			if (!signedMessage.isFullyFiltered()) {
				sender.networkHandler.sendChatMessage(signedMessage, params);
			}
		}
	}

	/**
	 * The wrapper used for messages without associated source profile.
	 */
	public static record Profileless(Text getContent) implements SentMessage {
		@Override
		public void send(ServerPlayerEntity sender, boolean filterMaskEnabled, MessageType.Parameters params) {
			sender.networkHandler.sendProfilelessChatMessage(this.getContent, params);
		}
	}
}
