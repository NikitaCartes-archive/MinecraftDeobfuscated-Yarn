package net.minecraft.network;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.network.encryption.ChatMessageSignature;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Chat decorator decorates the chat server-side. Currently, only one chat decorator
 * can exist at a time. The chat decorator that is currently used can be obtained by
 * {@link net.minecraft.server.MinecraftServer#getChatDecorator}.
 * 
 * <p>For the chat decorator to produce a signed message, <strong>both the server
 * and the sender's client need to have chat previews enabled</strong>, Otherwise, the decorated
 * content is considered unsigned, and if the clients require chat messages to be signed
 * via the {@linkplain net.minecraft.client.option.GameOptions#getOnlyShowSecureChat
 * "Only Show Secure Chat" option}, they will see the undecorated message. Therefore,
 * chat decorator is <strong>not recommended for censoring messages</strong>.
 * 
 * <p>It is <strong>very important that the decorator return the same text when previewed
 * and sent</strong>. If this is not followed correctly, the server detects that the client
 * sent a forged text and discards the message. For example, a decorator that appends the
 * time the decoration was applied would be likely to fail, since there is usually a delay
 * between the previewing and the submission. One way to solve this issue is to make it
 * cache the result on preview, so that when the sent message needs decorating, the cached
 * value can be used.
 */
@FunctionalInterface
public interface ChatDecorator {
	/**
	 * An empty chat decorator that does not decorate anything.
	 */
	ChatDecorator NOOP = (sender, message) -> CompletableFuture.completedFuture(message);

	/**
	 * {@return the signed chat message with unsigned content set as decorated {@code message}}
	 * 
	 * <p>If the received player requires signed chat message, they will see the original content.
	 */
	CompletableFuture<Text> decorate(@Nullable ServerPlayerEntity sender, Text message);

	/**
	 * {@return the decorated signed chat message from undecorated {@code message}}
	 * 
	 * <p>If {@code previewed} is false, the returned message will have the original
	 * content as signed and the decorated content as unsigned. This means that if the
	 * received player requires signed chat message, they will see the original content.
	 * 
	 * <p>If {@code message} has a filtered part, this will decorate both the raw and the
	 * filtered text.
	 */
	default CompletableFuture<FilteredMessage<Text>> decorateFiltered(@Nullable ServerPlayerEntity sender, FilteredMessage<Text> message) {
		CompletableFuture<Text> completableFuture = this.decorate(sender, message.raw());
		if (!message.isFiltered()) {
			return completableFuture.thenApply(FilteredMessage::permitted);
		} else if (message.filtered() == null) {
			return completableFuture.thenApply(FilteredMessage::censored);
		} else {
			CompletableFuture<Text> completableFuture2 = this.decorate(sender, message.filtered());
			return CompletableFuture.allOf(completableFuture, completableFuture2)
				.thenApply(void_ -> new FilteredMessage<>((Text)completableFuture.join(), (Text)completableFuture2.join()));
		}
	}

	/**
	 * {@return the decorated signed chat message from undecorated {@code message}}
	 * 
	 * <p>If {@code previewed} is false, the returned message will have the original
	 * content as signed and the decorated content as unsigned. This means that if the
	 * received player requires signed chat message, they will see the original content.
	 * 
	 * @param previewed whether the decoration was previewed by the sender's client
	 */
	default CompletableFuture<FilteredMessage<SignedChatMessage>> decorateChat(
		@Nullable ServerPlayerEntity sender, FilteredMessage<Text> message, ChatMessageSignature signature, boolean previewed
	) {
		return this.decorateFiltered(sender, message).thenApply(decorated -> SignedChatMessage.toSignedMessage(message, decorated, signature, previewed));
	}
}
