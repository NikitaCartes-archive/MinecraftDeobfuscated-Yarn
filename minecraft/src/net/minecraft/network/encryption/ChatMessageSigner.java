package net.minecraft.network.encryption;

import java.security.Signature;
import java.security.SignatureException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.text.Text;

public record ChatMessageSigner(UUID sender, Instant timeStamp, long salt) {
	public static ChatMessageSigner create(UUID sender) {
		return new ChatMessageSigner(sender, Instant.now(), NetworkEncryptionUtils.SecureRandomUtil.nextLong());
	}

	public ChatMessageSignature sign(Signature signature, Text message) throws SignatureException {
		ChatMessageSignature.updateSignature(signature, message, this.sender, this.timeStamp, this.salt);
		return new ChatMessageSignature(this.sender, this.timeStamp, new NetworkEncryptionUtils.SignatureData(this.salt, signature.sign()));
	}

	public ChatMessageSignature sign(Signature signature, String message) throws SignatureException {
		return this.sign(signature, Text.literal(message));
	}
}
