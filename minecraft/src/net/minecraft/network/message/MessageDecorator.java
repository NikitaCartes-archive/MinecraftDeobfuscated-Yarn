package net.minecraft.network.message;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
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
 * <p>Message decorator results are {@linkplain CachedDecoratorResult cached}, allowing
 * non-pure decorators (i.e. ones affected by externally controlled variables) without
 * affecting the signature verification process. Note that the decorator can still
 * run during message submission to decorate filtered parts of the message.
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

	default CompletableFuture<SignedMessage> decorate(@Nullable ServerPlayerEntity serverPlayerEntity, SignedMessage signedMessage) {
		return signedMessage.getSignedContent().isDecorated()
			? CompletableFuture.completedFuture(signedMessage)
			: this.decorate(serverPlayerEntity, signedMessage.getContent()).thenApply(signedMessage::withUnsignedContent);
	}

	static SignedMessage attachIfNotDecorated(SignedMessage signedMessage, Text text) {
		return !signedMessage.getSignedContent().isDecorated() ? signedMessage.withUnsignedContent(text) : signedMessage;
	}
}
