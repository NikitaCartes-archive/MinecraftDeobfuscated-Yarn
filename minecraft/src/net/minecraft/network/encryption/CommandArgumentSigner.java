package net.minecraft.network.encryption;

import java.time.Instant;
import java.util.UUID;

/**
 * A signer for command arguments.
 */
public interface CommandArgumentSigner {
	CommandArgumentSigner NONE = argumentName -> ChatMessageSignature.none();

	ChatMessageSignature getArgumentSignature(String argumentName);

	default boolean isPreviewSigned(String argumentName) {
		return false;
	}

	/**
	 * A signature for command arguments, consisting of the sender, the timestamp,
	 * and the signature datas for the arguments.
	 */
	public static record Signatures(UUID sender, Instant timestamp, ArgumentSignatureDataMap argumentSignatures, boolean signedPreview)
		implements CommandArgumentSigner {
		@Override
		public ChatMessageSignature getArgumentSignature(String string) {
			NetworkEncryptionUtils.SignatureData signatureData = this.argumentSignatures.get(string);
			return signatureData != null ? new ChatMessageSignature(this.sender, this.timestamp, signatureData) : ChatMessageSignature.none();
		}

		@Override
		public boolean isPreviewSigned(String argumentName) {
			return this.signedPreview;
		}
	}
}
