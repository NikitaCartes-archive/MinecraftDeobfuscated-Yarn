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

	default CompletableFuture<FilteredMessage<Text>> rebuildFiltered(
		@Nullable ServerPlayerEntity serverPlayerEntity, FilteredMessage<Text> filteredMessage, Text text
	) {
		return filteredMessage.method_45001(text, textx -> this.decorate(serverPlayerEntity, textx));
	}

	static FilteredMessage<SignedMessage> attachUnsignedDecoration(FilteredMessage<SignedMessage> message, FilteredMessage<Text> decorated) {
		return message.map(
			rawMessage -> rawMessage.withUnsignedContent(decorated.raw()),
			filteredMessage -> decorated.filtered() != null ? filteredMessage.withUnsignedContent(decorated.filtered()) : filteredMessage
		);
	}
}
