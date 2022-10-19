package net.minecraft.network.message;

import javax.annotation.Nullable;
import net.minecraft.network.encryption.SignatureVerifier;

/**
 * Verifies incoming messages' signature and the message chain.
 * 
 * <p>Methods in this interface must be called in the order of the message's reception,
 * as it affects the verification result.
 */
@FunctionalInterface
public interface MessageVerifier {
	MessageVerifier NO_SIGNATURE = message -> !message.hasSignature();
	MessageVerifier UNVERIFIED = message -> false;

	boolean isVerified(SignedMessage message);

	public static class Impl implements MessageVerifier {
		private final SignatureVerifier signatureVerifier;
		@Nullable
		private SignedMessage lastVerifiedMessage;
		private boolean lastMessageVerified = true;

		public Impl(SignatureVerifier signatureVerifier) {
			this.signatureVerifier = signatureVerifier;
		}

		private boolean verifyPrecedingSignature(SignedMessage message) {
			return message.equals(this.lastVerifiedMessage) ? true : this.lastVerifiedMessage == null || message.link().linksTo(this.lastVerifiedMessage.link());
		}

		@Override
		public boolean isVerified(SignedMessage message) {
			this.lastMessageVerified = this.lastMessageVerified && message.verify(this.signatureVerifier) && this.verifyPrecedingSignature(message);
			if (!this.lastMessageVerified) {
				return false;
			} else {
				this.lastVerifiedMessage = message;
				return true;
			}
		}
	}
}
