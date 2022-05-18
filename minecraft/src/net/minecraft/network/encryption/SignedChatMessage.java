package net.minecraft.network.encryption;

import java.util.Optional;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

/**
 * A signed chat message, consisting of the signature, the signed content,
 * and the optional unsigned content supplied when the chat decorator produced
 * unsigned message due to the chat preview being disabled on either side.
 * 
 * <p>Note that the signature itself might not be valid.
 */
public record SignedChatMessage(Text signedContent, ChatMessageSignature signature, Optional<Text> unsignedContent) {
	/**
	 * {@return a new signed chat message with {@code signedContent} and {@code signature}}
	 */
	public static SignedChatMessage of(Text signedContent, ChatMessageSignature signature) {
		return new SignedChatMessage(signedContent, signature, Optional.empty());
	}

	/**
	 * {@return a new signed chat message with {@code signedContent} and {@code signature}}
	 */
	public static SignedChatMessage of(String signedContent, ChatMessageSignature signature) {
		return of(Text.literal(signedContent), signature);
	}

	/**
	 * {@return a new signed chat message from the content supplied by the chat decorator}
	 * 
	 * @implNote If the decorated content is different from the original and is not
	 * previewed, this will create a signed chat message with the signed original content
	 * and the unsigned decorated content. Otherwise, this will create a signed chat message
	 * with the signed decorated content.
	 */
	public static SignedChatMessage of(Text originalContent, Text decoratedContent, ChatMessageSignature signature, boolean previewed) {
		if (originalContent.equals(decoratedContent)) {
			return of(originalContent, signature);
		} else {
			return !previewed ? of(originalContent, signature).withUnsigned(decoratedContent) : of(decoratedContent, signature);
		}
	}

	public static FilteredMessage<SignedChatMessage> toSignedMessage(
		FilteredMessage<Text> original, FilteredMessage<Text> decorated, ChatMessageSignature signature, boolean preview
	) {
		Text text = original.raw();
		Text text2 = decorated.raw();
		SignedChatMessage signedChatMessage = of(text, text2, signature, preview);
		if (decorated.isFiltered()) {
			SignedChatMessage signedChatMessage2 = Util.map(decorated.filtered(), SignedChatMessage::of);
			return new FilteredMessage<>(signedChatMessage, signedChatMessage2);
		} else {
			return FilteredMessage.permitted(signedChatMessage);
		}
	}

	/**
	 * {@return a new signed chat message with {@code signedContent} and "none" signature}
	 */
	public static SignedChatMessage of(Text content) {
		return new SignedChatMessage(content, ChatMessageSignature.none(), Optional.empty());
	}

	/**
	 * {@return the new signed chat message with {@code unsignedContent} added}
	 * 
	 * @apiNote This is used in vanilla when chat decorator decorates the message without
	 * the client previewing it. In this case, the undecorated content is signed but the
	 * decorated content is unsigned.
	 */
	public SignedChatMessage withUnsigned(Text unsignedContent) {
		return new SignedChatMessage(this.signedContent, this.signature, Optional.of(unsignedContent));
	}

	/**
	 * {@return whether the message can be verified using the public key}
	 */
	public boolean verify(PlayerPublicKey key) {
		return this.signature.verify(key.createSignatureInstance(), this.signedContent);
	}

	/**
	 * {@return whether the message can be verified using the public key <strong>or if the
	 * player does not have the key</strong>}
	 */
	public boolean verify(ServerPlayerEntity player) {
		PlayerPublicKey playerPublicKey = player.getPublicKey();
		return playerPublicKey == null || this.verify(playerPublicKey);
	}

	/**
	 * {@return whether the message can be verified using the public key of the source
	 * <strong>or if the source does not have the key</strong>}
	 */
	public boolean verify(ServerCommandSource source) {
		ServerPlayerEntity serverPlayerEntity = source.getPlayer();
		return serverPlayerEntity == null || this.verify(serverPlayerEntity);
	}

	/**
	 * {@return the content of the message}
	 * 
	 * <p>This returns the unsigned content if present, and fallbacks to the signed content.
	 */
	public Text getContent() {
		return (Text)this.unsignedContent.orElse(this.signedContent);
	}
}
