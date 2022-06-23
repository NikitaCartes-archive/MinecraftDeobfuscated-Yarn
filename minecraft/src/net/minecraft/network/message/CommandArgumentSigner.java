package net.minecraft.network.message;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.encryption.NetworkEncryptionUtils;

/**
 * A signer for command arguments.
 */
public interface CommandArgumentSigner {
	CommandArgumentSigner NONE = argumentName -> MessageSignature.none();

	MessageSignature getArgumentSignature(String argumentName);

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
		public MessageSignature getArgumentSignature(String string) {
			NetworkEncryptionUtils.SignatureData signatureData = this.argumentSignatures.get(string);
			return signatureData != null ? new MessageSignature(this.sender, this.timestamp, signatureData) : MessageSignature.none();
		}

		@Override
		public boolean isPreviewSigned(String argumentName) {
			return this.signedPreview;
		}
	}
}
