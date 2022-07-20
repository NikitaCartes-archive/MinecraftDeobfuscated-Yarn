package net.minecraft.client.network.message;

import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.util.Deque;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.HeaderEntry;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.MessageVerifier;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

/**
 * Handles received messages, including chat messages and game messages.
 */
@Environment(EnvType.CLIENT)
public class MessageHandler {
	private static final Text field_39904 = Text.translatable("multiplayer.disconnect.chat_validation_failed");
	private final MinecraftClient client;
	private final Deque<MessageHandler.ProcessableMessage> delayedMessages = Queues.<MessageHandler.ProcessableMessage>newArrayDeque();
	private long chatDelay;
	private long lastProcessTime;

	public MessageHandler(MinecraftClient client) {
		this.client = client;
	}

	/**
	 * Processes all delayed messages until one of them fails to process if the delay
	 * has passed, and otherwise does nothing.
	 */
	public void processDelayedMessages() {
		if (this.chatDelay != 0L) {
			if (Util.getMeasuringTimeMs() >= this.lastProcessTime + this.chatDelay) {
				MessageHandler.ProcessableMessage processableMessage = (MessageHandler.ProcessableMessage)this.delayedMessages.poll();

				while (processableMessage != null && !processableMessage.accept()) {
					processableMessage = (MessageHandler.ProcessableMessage)this.delayedMessages.poll();
				}
			}
		}
	}

	/**
	 * Sets the chat delay to {@code chatDelay} seconds. If the chat delay was changed
	 * to {@code 0}, this also processes all queued messages.
	 */
	public void setChatDelay(double chatDelay) {
		long l = (long)(chatDelay * 1000.0);
		if (l == 0L && this.chatDelay > 0L) {
			this.delayedMessages.forEach(MessageHandler.ProcessableMessage::accept);
			this.delayedMessages.clear();
		}

		this.chatDelay = l;
	}

	/**
	 * Processes one delayed message from the queue's beginning.
	 */
	public void process() {
		((MessageHandler.ProcessableMessage)this.delayedMessages.remove()).accept();
	}

	/**
	 * {@return the number of delayed messages that are not processed yet}
	 */
	public long getUnprocessedMessageCount() {
		return this.delayedMessages.stream().filter(MessageHandler.ProcessableMessage::isUnprocessed).count();
	}

	/**
	 * Processes all delayed messages from the queue.
	 */
	public void processAll() {
		this.delayedMessages.forEach(message -> {
			message.markProcessed();
			message.accept();
		});
		this.delayedMessages.clear();
	}

	/**
	 * Removes a delayed message whose signature matches {@code signature}.
	 * If this returns {@code false}, either the message is not received or it it
	 * already on the hud.
	 * 
	 * @return whether the message was removed
	 */
	public boolean removeDelayedMessage(MessageSignatureData signature) {
		for (MessageHandler.ProcessableMessage processableMessage : this.delayedMessages) {
			if (processableMessage.removeMatching(signature)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * {@return if the chat delay is set and the message should be delayed}
	 */
	private boolean shouldDelay() {
		return this.chatDelay > 0L && Util.getMeasuringTimeMs() < this.lastProcessTime + this.chatDelay;
	}

	/**
	 * Queues {@code processor} during {@linkplain #shouldDelay the chat delay},
	 * otherwise runs the processor.
	 */
	private void process(MessageHandler.ProcessableMessage message) {
		if (this.shouldDelay()) {
			this.delayedMessages.add(message);
		} else {
			message.accept();
		}
	}

	/**
	 * Called when a chat message is received.
	 * 
	 * <p>This enqueues the message to be processed after the chat delay set in
	 * options, if any.
	 * 
	 * @see #processChatMessage
	 */
	public void onChatMessage(SignedMessage message, MessageType.Parameters params) {
		final boolean bl = this.client.options.getOnlyShowSecureChat().getValue();
		final SignedMessage signedMessage = bl ? message.withoutUnsigned() : message;
		final Text text = params.applyChatDecoration(signedMessage.getContent());
		MessageMetadata messageMetadata = message.createMetadata();
		if (!messageMetadata.lacksSender()) {
			final PlayerListEntry playerListEntry = this.getPlayerListEntry(messageMetadata.sender());
			final Instant instant = Instant.now();
			this.process(new MessageHandler.ProcessableMessage() {
				private boolean processed;

				@Override
				public boolean accept() {
					if (this.processed) {
						byte[] bs = message.signedBody().digest().asBytes();
						MessageHandler.this.processHeader(message.signedHeader(), message.headerSignature(), bs);
						return false;
					} else {
						return MessageHandler.this.processChatMessage(params, message, text, playerListEntry, bl, instant);
					}
				}

				@Override
				public boolean removeMatching(MessageSignatureData signature) {
					if (message.headerSignature().equals(signature)) {
						this.processed = true;
						return true;
					} else {
						return false;
					}
				}

				@Override
				public void markProcessed() {
					this.processed = true;
				}

				@Override
				public boolean isUnprocessed() {
					return !this.processed;
				}
			});
		} else {
			this.process(new MessageHandler.ProcessableMessage() {
				@Override
				public boolean accept() {
					return MessageHandler.this.processProfilelessMessage(params, signedMessage, text);
				}

				@Override
				public boolean isUnprocessed() {
					return true;
				}
			});
		}
	}

	/**
	 * Called when a message header is received.
	 * 
	 * <p>Message header is received instead of the full message when a message is censored
	 * or when the message is originally sent without metadata due to it being originated from
	 * entities. This is to keep the integrity of the "message chain".
	 */
	public void onMessageHeader(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
		this.process(() -> this.processHeader(header, signature, bodyDigest));
	}

	/**
	 * Processes a chat message and sends acknowledgment to the server.
	 * 
	 * <p>The message can still end up not being displayed if the verification
	 * fails and {@code onlyShowSecureChat} is {@code true} or if the sender is
	 * blocked via the social interactions screen.
	 * 
	 * @return whether the message was actually displayed
	 * @see #processChatMessageInternal
	 * 
	 * @param receptionTimestamp the timestamp when the message was received by this client
	 */
	boolean processChatMessage(
		MessageType.Parameters params,
		SignedMessage message,
		Text decorated,
		@Nullable PlayerListEntry senderEntry,
		boolean onlyShowSecureChat,
		Instant receptionTimestamp
	) {
		boolean bl = this.processChatMessageInternal(params, message, decorated, senderEntry, onlyShowSecureChat, receptionTimestamp);
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.acknowledge(message, bl);
		}

		return bl;
	}

	/**
	 * Processes a chat message.
	 * 
	 * <p>The message can still end up not being displayed if the verification
	 * fails and {@code onlyShowSecureChat} is {@code true} or if the sender is
	 * blocked via the social interactions screen.
	 * 
	 * <p>This adds the message to the hud, narrates it, and appends it to the
	 * chat log.
	 * 
	 * @return whether the message was actually displayed
	 * 
	 * @param receptionTimestamp the timestamp when the message was received by this client
	 */
	private boolean processChatMessageInternal(
		MessageType.Parameters params,
		SignedMessage message,
		Text decorated,
		@Nullable PlayerListEntry senderEntry,
		boolean onlyShowSecureChat,
		Instant receptionTimestamp
	) {
		MessageTrustStatus messageTrustStatus = this.getStatus(message, decorated, senderEntry, receptionTimestamp);
		if (messageTrustStatus == MessageTrustStatus.BROKEN_CHAIN) {
			this.method_45031();
			return true;
		} else if (onlyShowSecureChat && messageTrustStatus.isInsecure()) {
			return false;
		} else if (this.client.shouldBlockMessages(message.createMetadata().sender())) {
			return false;
		} else {
			MessageIndicator messageIndicator = messageTrustStatus.createIndicator(message);
			this.client.inGameHud.getChatHud().addMessage(decorated, message.headerSignature(), messageIndicator);
			this.narrate(params, message);
			this.addToChatLog(message, params, senderEntry, messageTrustStatus);
			this.lastProcessTime = Util.getMeasuringTimeMs();
			return true;
		}
	}

	/**
	 * Processes a message that is sent as chat message but lacks the sender.
	 * 
	 * <p>This is usually a message sent via commands executed from {@code /execute}
	 * command.
	 * 
	 * <p>This adds the message to the hud, narrates it, and appends it to the
	 * chat log. The message is not verified.
	 */
	boolean processProfilelessMessage(MessageType.Parameters params, SignedMessage message, Text decorated) {
		this.client.inGameHud.getChatHud().addMessage(decorated, MessageIndicator.system());
		this.narrate(params, message);
		this.addToChatLog(decorated, message.getTimestamp());
		this.lastProcessTime = Util.getMeasuringTimeMs();
		return true;
	}

	/**
	 * Processes a received message header.
	 * 
	 * <p>Message header is received instead of the full message when a message is censored
	 * or when the message is originally sent without metadata due to it being originated from
	 * entities. This is to keep the integrity of the "message chain".
	 * 
	 * <p>This stores the header verification result and adds it to the chat log.
	 * 
	 * @see net.minecraft.network.message.MessageVerifier#storeHeaderVerification
	 */
	boolean processHeader(MessageHeader messageHeader, MessageSignatureData messageSignatureData, byte[] bodyDigest) {
		PlayerListEntry playerListEntry = this.getPlayerListEntry(messageHeader.sender());
		if (playerListEntry != null) {
			MessageVerifier.class_7646 lv = playerListEntry.getMessageVerifier().storeHeaderVerification(messageHeader, messageSignatureData, bodyDigest);
			if (lv == MessageVerifier.class_7646.BROKEN_CHAIN) {
				this.method_45031();
				return true;
			}
		}

		this.addToChatLog(messageHeader, messageSignatureData, bodyDigest);
		return false;
	}

	private void method_45031() {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.getConnection().disconnect(field_39904);
		}
	}

	/**
	 * Narrates {@code message}.
	 * 
	 * @see net.minecraft.client.util.NarratorManager#narrateChatMessage
	 */
	private void narrate(MessageType.Parameters params, SignedMessage message) {
		this.client.getNarratorManager().narrateChatMessage(() -> params.applyNarrationDecoration(message.getContent()));
	}

	/**
	 * {@return the trust status of {@code message}}
	 * 
	 * <p>This returns {@link MessageTrustStatus#SECURE} for messages that are
	 * considered to be {@linkplain #isAlwaysTrusted always trusted}.
	 * 
	 * @see #isAlwaysTrusted
	 * @see MessageTrustStatus#getStatus
	 */
	private MessageTrustStatus getStatus(SignedMessage message, Text decorated, @Nullable PlayerListEntry senderEntry, Instant receptionTimestamp) {
		return this.isAlwaysTrusted(message.createMetadata().sender())
			? MessageTrustStatus.SECURE
			: MessageTrustStatus.getStatus(message, decorated, senderEntry, receptionTimestamp);
	}

	private void addToChatLog(SignedMessage message, MessageType.Parameters params, @Nullable PlayerListEntry senderEntry, MessageTrustStatus trustStatus) {
		GameProfile gameProfile;
		if (senderEntry != null) {
			gameProfile = senderEntry.getProfile();
		} else {
			gameProfile = new GameProfile(message.createMetadata().sender(), params.name().getString());
		}

		ChatLog chatLog = this.client.getAbuseReportContext().chatLog();
		chatLog.add(ReceivedMessage.of(gameProfile, params.name(), message, trustStatus));
	}

	private void addToChatLog(Text message, Instant timestamp) {
		ChatLog chatLog = this.client.getAbuseReportContext().chatLog();
		chatLog.add(ReceivedMessage.of(message, timestamp));
	}

	private void addToChatLog(MessageHeader header, MessageSignatureData signatures, byte[] bodyDigest) {
		ChatLog chatLog = this.client.getAbuseReportContext().chatLog();
		chatLog.add(HeaderEntry.of(header, signatures, bodyDigest));
	}

	/**
	 * {@return the player list entry for {@code sender}, or {@code null} if the sender's
	 * UUID did not correspond to any known players}
	 */
	@Nullable
	private PlayerListEntry getPlayerListEntry(UUID sender) {
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
		return clientPlayNetworkHandler != null ? clientPlayNetworkHandler.getPlayerListEntry(sender) : null;
	}

	/**
	 * Called when a game message is received.
	 * 
	 * <p>Game messages ignore chat delay.
	 */
	public void onGameMessage(Text message, boolean overlay) {
		if (!this.client.options.getHideMatchedNames().getValue() || !this.client.shouldBlockMessages(this.extractSender(message))) {
			if (overlay) {
				this.client.inGameHud.setOverlayMessage(message, false);
			} else {
				this.client.inGameHud.getChatHud().addMessage(message, MessageIndicator.system());
				this.addToChatLog(message, Instant.now());
			}

			this.client.getNarratorManager().narrate(message);
		}
	}

	private UUID extractSender(Text text) {
		String string = TextVisitFactory.removeFormattingCodes(text);
		String string2 = StringUtils.substringBetween(string, "<", ">");
		return string2 == null ? Util.NIL_UUID : this.client.getSocialInteractionsManager().getUuid(string2);
	}

	/**
	 * {@return whether messages from {@code sender} are always trusted}
	 * 
	 * <p>Messages from this client's player in a singleplayer world are always trusted.
	 */
	private boolean isAlwaysTrusted(UUID sender) {
		if (this.client.isInSingleplayer() && this.client.player != null) {
			UUID uUID = this.client.player.getGameProfile().getId();
			return uUID.equals(sender);
		} else {
			return false;
		}
	}

	/**
	 * A message to be processed. An instance is created for each received message.
	 */
	@Environment(EnvType.CLIENT)
	interface ProcessableMessage {
		/**
		 * If {@code signature} equals this message's signature, marks this
		 * as processed and returns {@code true}. Otherwise, returns {@code false}.
		 * 
		 * @return whether the passed signature matches the message's signature
		 */
		default boolean removeMatching(MessageSignatureData signature) {
			return false;
		}

		/**
		 * Marks this as processed.
		 */
		default void markProcessed() {
		}

		/**
		 * If this is not processed yet, adds the message to the hud; otherwise, processes
		 * the message header without adding to the hud.
		 */
		boolean accept();

		/**
		 * {@return {@code true} if this is not processed yet}
		 */
		default boolean isUnprocessed() {
			return false;
		}
	}
}
