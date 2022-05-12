package net.minecraft.network.encryption;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Optional;
import net.minecraft.text.Text;

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
	 * {@return whether the message can be verified using {@code signature}}
	 * 
	 * @throws SignatureException when verifying fails
	 */
	public boolean verify(Signature signature) throws SignatureException {
		return this.signature.verify(signature, this.signedContent);
	}

	/**
	 * {@return whether the message can be verified using the public key}
	 */
	public boolean verify(PlayerPublicKey key) {
		if (!this.signature.canVerify()) {
			return false;
		} else {
			try {
				return this.verify(key.createSignatureInstance());
			} catch (NetworkEncryptionException | GeneralSecurityException var3) {
				return false;
			}
		}
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
