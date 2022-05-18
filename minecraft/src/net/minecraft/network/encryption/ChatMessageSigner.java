package net.minecraft.network.encryption;

import java.security.SignatureException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.text.Text;

/**
 * A signer for chat messages that produces {@link ChatMessageSignature}.
 */
public record ChatMessageSigner(UUID sender, Instant timeStamp, long salt) {
	/**
	 * {@return a new signer instance}
	 */
	public static ChatMessageSigner create(UUID sender) {
		return new ChatMessageSigner(sender, Instant.now(), NetworkEncryptionUtils.SecureRandomUtil.nextLong());
	}

	/**
	 * {@return the chat message signature obtained by signing {@code message} with {@code signature}}
	 */
	public ChatMessageSignature sign(Signer signer, Text message) {
		byte[] bs = signer.sign(updater -> ChatMessageSignature.updateSignature(updater, message, this.sender, this.timeStamp, this.salt));
		return new ChatMessageSignature(this.sender, this.timeStamp, new NetworkEncryptionUtils.SignatureData(this.salt, bs));
	}

	/**
	 * {@return the chat message signature obtained by signing {@code message} with {@code signature}}
	 */
	public ChatMessageSignature sign(Signer signer, String message) throws SignatureException {
		return this.sign(signer, Text.literal(message));
	}
}
