package net.minecraft.network.message;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Message decorator decorates the chat messages and other messages server-side.
 * Currently, only one message decorator can exist at a time. The message decorator
 * that is currently used can be obtained by
 * {@link net.minecraft.server.MinecraftServer#getMessageDecorator}.
 * 
 * <p>For the message decorator to produce a signed message, <strong>both the server
 * and the sender's client need to have chat previews enabled</strong>, Otherwise, the decorated
 * content is considered unsigned, and if the clients require chat messages to be signed
 * via the {@linkplain net.minecraft.client.option.GameOptions#getOnlyShowSecureChat
 * "Only Show Secure Chat" option}, they will see the undecorated message. Therefore,
 * message decorator is <strong>not recommended for censoring messages</strong>.
 * 
 * <p>It is <strong>very important that the decorator be pure; i.e. return the
 * same text when given the same text (and sender)</strong>. Otherwise, the server
 * detects a mismatch between the preview and the actual message, and discards the message
 * as it is now considered improperly signed. For example, a decorator that appends
 * the time the decoration was applied would be likely to fail, since there is usually
 * a delay between the previewing and the submission.
 */
@FunctionalInterface
public interface MessageDecorator {
	/**
	 * An empty message decorator that returns the original message.
	 */
	MessageDecorator NOOP = (sender, message) -> CompletableFuture.completedFuture(message);

	/**
	 * {@return the decorated {@code message}}
	 * 
	 * @param sender the player who sent the message, or {@code null} if {@code message} was not
	 * sent by a player
	 */
	CompletableFuture<Text> decorate(@Nullable ServerPlayerEntity sender, Text message);

	/**
	 * {@return the decorated filtered message from undecorated {@code message}}
	 * 
	 * <p>This keeps the filtered status of the original message; i.e. fully censored messages
	 * will remain fully censored, and unfiltered messages will remain unfiltered. If the message
	 * is partially filtered, both the raw and the filtered message will be decorated.
	 */
	default CompletableFuture<FilteredMessage<Text>> decorateFiltered(@Nullable ServerPlayerEntity sender, FilteredMessage<Text> message) {
		CompletableFuture<Text> completableFuture = this.decorate(sender, message.raw());
		if (message.filtered() == null) {
			return completableFuture.thenApply(FilteredMessage::censored);
		} else if (!message.isFiltered()) {
			return completableFuture.thenApply(FilteredMessage::permitted);
		} else {
			CompletableFuture<Text> completableFuture2 = this.decorate(sender, message.filtered());
			return CompletableFuture.allOf(completableFuture, completableFuture2)
				.thenApply(void_ -> new FilteredMessage<>((Text)completableFuture.join(), (Text)completableFuture2.join()));
		}
	}

	default CompletableFuture<FilteredMessage<SignedMessage>> decorateSignedChat(@Nullable ServerPlayerEntity sender, FilteredMessage<SignedMessage> message) {
		FilteredMessage<Text> filteredMessage = message.map(SignedMessage::getSignedContent);
		return this.decorateFiltered(sender, filteredMessage).thenApply(decoratedMessage -> attachDecoration(message, decoratedMessage));
	}

	static FilteredMessage<SignedMessage> attachDecoration(FilteredMessage<SignedMessage> message, FilteredMessage<Text> decoratedMessage) {
		return message.map(
			rawMessage -> rawMessage.withUnsignedContent(decoratedMessage.raw()),
			filteredMessage -> decoratedMessage.filtered() != null ? filteredMessage.withUnsignedContent(decoratedMessage.filtered()) : filteredMessage
		);
	}
}
