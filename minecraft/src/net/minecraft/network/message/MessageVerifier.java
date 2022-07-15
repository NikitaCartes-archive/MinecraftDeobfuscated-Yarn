package net.minecraft.network.message;

import javax.annotation.Nullable;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;

/**
 * Verifies incoming messages' signature and the message chain.
 * 
 * <p>Methods in this interface must be called in the order of the message's reception,
 * as it affects the verification result.
 */
public interface MessageVerifier {
	MessageVerifier UNVERIFIABLE = new MessageVerifier() {
		@Override
		public void storeHeaderVerification(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
		}

		@Override
		public boolean verify(SignedMessage message) {
			return false;
		}
	};

	static MessageVerifier create(@Nullable PlayerPublicKey publicKey) {
		return (MessageVerifier)(publicKey != null ? new MessageVerifier.Impl(publicKey.createSignatureInstance()) : UNVERIFIABLE);
	}

	/**
	 * Stores the status of verifying the header.
	 * 
	 * <p>Clients can receive only the message header instead of the whole message. This
	 * allows the chain to reference such messages. Since no actual content is received,
	 * this does not return the verification status.
	 */
	void storeHeaderVerification(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest);

	boolean verify(SignedMessage message);

	public static class Impl implements MessageVerifier {
		private final SignatureVerifier signatureVerifier;
		@Nullable
		private MessageSignatureData precedingSignature;
		boolean lastMessageVerified = true;

		public Impl(SignatureVerifier signatureVerifier) {
			this.signatureVerifier = signatureVerifier;
		}

		private boolean verifyPrecedingSignature(MessageHeader header, MessageSignatureData signature) {
			return this.precedingSignature == null || this.precedingSignature.equals(header.precedingSignature()) || this.precedingSignature.equals(signature);
		}

		private boolean verify(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
			return this.verifyPrecedingSignature(header, signature) && signature.verify(this.signatureVerifier, header, bodyDigest);
		}

		@Override
		public void storeHeaderVerification(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
			this.lastMessageVerified = this.lastMessageVerified && this.verify(header, signature, bodyDigest);
			this.precedingSignature = signature;
		}

		@Override
		public boolean verify(SignedMessage message) {
			if (this.lastMessageVerified && this.verify(message.signedHeader(), message.headerSignature(), message.signedBody().digest().asBytes())) {
				this.precedingSignature = message.headerSignature();
				return true;
			} else {
				this.lastMessageVerified = true;
				this.precedingSignature = null;
				return false;
			}
		}
	}
}
