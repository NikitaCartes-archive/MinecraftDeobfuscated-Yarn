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
	MessageVerifier NOOP = new MessageVerifier() {
		@Override
		public void storeHeaderVerification(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
		}

		@Override
		public boolean verify(SignedMessage message) {
			return true;
		}
	};

	static MessageVerifier create(@Nullable PlayerPublicKey publicKey) {
		return (MessageVerifier)(publicKey != null ? new MessageVerifier.Impl(publicKey.createSignatureInstance()) : NOOP);
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

		private boolean verifyChain(MessageHeader header, MessageSignatureData signature) {
			boolean bl = this.precedingSignature == null || this.precedingSignature.equals(header.precedingSignature()) || this.precedingSignature.equals(signature);
			this.precedingSignature = signature;
			return bl;
		}

		@Override
		public void storeHeaderVerification(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
			boolean bl = signature.verify(this.signatureVerifier, header, bodyDigest);
			boolean bl2 = this.verifyChain(header, signature);
			this.lastMessageVerified = this.lastMessageVerified && bl && bl2;
		}

		@Override
		public boolean verify(SignedMessage message) {
			byte[] bs = message.signedBody().digest().asBytes();
			boolean bl = message.headerSignature().verify(this.signatureVerifier, message.signedHeader(), bs);
			boolean bl2 = this.verifyChain(message.signedHeader(), message.headerSignature());
			boolean bl3 = this.lastMessageVerified && bl && bl2;
			this.lastMessageVerified = bl;
			return bl3;
		}
	}
}
