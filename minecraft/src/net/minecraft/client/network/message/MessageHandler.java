package net.minecraft.client.network.message;

import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.util.Deque;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

/**
 * Handles received messages, including chat messages and game messages.
 */
@Environment(EnvType.CLIENT)
public class MessageHandler {
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
		return (long)this.delayedMessages.size();
	}

	/**
	 * Processes all delayed messages from the queue.
	 */
	public void processAll() {
		this.delayedMessages.forEach(MessageHandler.ProcessableMessage::accept);
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
		return this.delayedMessages.removeIf(message -> signature.equals(message.signature()));
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
	private void process(@Nullable MessageSignatureData signature, BooleanSupplier processor) {
		if (this.shouldDelay()) {
			this.delayedMessages.add(new MessageHandler.ProcessableMessage(signature, processor));
		} else {
			processor.getAsBoolean();
		}
	}

	public void onChatMessage(SignedMessage message, GameProfile sender, MessageType.Parameters params) {
		boolean bl = this.client.options.getOnlyShowSecureChat().getValue();
		SignedMessage signedMessage = bl ? message.withoutUnsigned() : message;
		Text text = params.applyChatDecoration(signedMessage.getContent());
		Instant instant = Instant.now();
		this.process(message.signature(), () -> {
			boolean bl2 = this.processChatMessageInternal(params, message, text, sender, bl, instant);
			ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
			if (clientPlayNetworkHandler != null) {
				clientPlayNetworkHandler.acknowledge(message, bl2);
			}

			return bl2;
		});
	}

	public void onProfilelessMessage(Text content, MessageType.Parameters params) {
		Instant instant = Instant.now();
		this.process(null, () -> {
			Text text2 = params.applyChatDecoration(content);
			this.client.inGameHud.getChatHud().addMessage(text2);
			this.narrate(params, content);
			this.addToChatLog(text2, instant);
			this.lastProcessTime = Util.getMeasuringTimeMs();
			return true;
		});
	}

	/**
	 * Processes a chat message.
	 * 
	 * <p>If the message cannot be verified due to a broken chain, this disconnects
	 * the client from the server.
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
		MessageType.Parameters params, SignedMessage message, Text decorated, GameProfile sender, boolean onlyShowSecureChat, Instant receptionTimestamp
	) {
		MessageTrustStatus messageTrustStatus = this.getStatus(message, decorated, receptionTimestamp);
		if (onlyShowSecureChat && messageTrustStatus.isInsecure()) {
			return false;
		} else if (!this.client.shouldBlockMessages(message.getSender()) && !message.isFullyFiltered()) {
			MessageIndicator messageIndicator = messageTrustStatus.createIndicator(message);
			MessageSignatureData messageSignatureData = message.signature();
			FilterMask filterMask = message.filterMask();
			if (filterMask.isPassThrough()) {
				this.client.inGameHud.getChatHud().addMessage(decorated, messageSignatureData, messageIndicator);
				this.narrate(params, message.getContent());
			} else {
				Text text = filterMask.getFilteredText(message.getSignedContent());
				if (text != null) {
					this.client.inGameHud.getChatHud().addMessage(params.applyChatDecoration(text), messageSignatureData, messageIndicator);
					this.narrate(params, text);
				}
			}

			this.addToChatLog(message, params, sender, messageTrustStatus);
			this.lastProcessTime = Util.getMeasuringTimeMs();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Narrates {@code message}.
	 * 
	 * @see net.minecraft.client.util.NarratorManager#narrateChatMessage
	 */
	private void narrate(MessageType.Parameters params, Text message) {
		this.client.getNarratorManager().narrateChatMessage(params.applyNarrationDecoration(message));
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
	private MessageTrustStatus getStatus(SignedMessage message, Text decorated, Instant receptionTimestamp) {
		return this.isAlwaysTrusted(message.getSender()) ? MessageTrustStatus.SECURE : MessageTrustStatus.getStatus(message, decorated, receptionTimestamp);
	}

	private void addToChatLog(SignedMessage message, MessageType.Parameters params, GameProfile sender, MessageTrustStatus trustStatus) {
		ChatLog chatLog = this.client.getAbuseReportContext().getChatLog();
		chatLog.add(ReceivedMessage.of(sender, message, trustStatus));
	}

	private void addToChatLog(Text message, Instant timestamp) {
		ChatLog chatLog = this.client.getAbuseReportContext().getChatLog();
		chatLog.add(ReceivedMessage.of(message, timestamp));
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
				this.client.inGameHud.getChatHud().addMessage(message);
				this.addToChatLog(message, Instant.now());
			}

			this.client.getNarratorManager().narrateSystemMessage(message);
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
	static record ProcessableMessage(@Nullable MessageSignatureData signature, BooleanSupplier handler) {
		/**
		 * If this is not processed yet, adds the message to the hud; otherwise, processes
		 * the message header without adding to the hud.
		 */
		public boolean accept() {
			return this.handler.getAsBoolean();
		}
	}
}
