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
		public MessageVerifier.class_7646 storeHeaderVerification(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
			return MessageVerifier.class_7646.NOT_SECURE;
		}

		@Override
		public MessageVerifier.class_7646 verify(SignedMessage message) {
			return MessageVerifier.class_7646.NOT_SECURE;
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
	MessageVerifier.class_7646 storeHeaderVerification(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest);

	MessageVerifier.class_7646 verify(SignedMessage message);

	public static class Impl implements MessageVerifier {
		private final SignatureVerifier signatureVerifier;
		@Nullable
		private MessageSignatureData precedingSignature;
		private boolean lastMessageVerified = true;

		public Impl(SignatureVerifier signatureVerifier) {
			this.signatureVerifier = signatureVerifier;
		}

		private boolean verifyPrecedingSignature(MessageHeader header, MessageSignatureData signature) {
			return signature.isEmpty()
				? false
				: this.precedingSignature == null || this.precedingSignature.equals(header.precedingSignature()) || this.precedingSignature.equals(signature);
		}

		private boolean verify(MessageHeader messageHeader, MessageSignatureData signature, byte[] bodyDigest) {
			return signature.verify(this.signatureVerifier, messageHeader, bodyDigest);
		}

		private MessageVerifier.class_7646 method_45048(MessageHeader messageHeader, MessageSignatureData messageSignatureData, byte[] bs) {
			this.lastMessageVerified = this.lastMessageVerified && this.verifyPrecedingSignature(messageHeader, messageSignatureData);
			if (!this.lastMessageVerified) {
				return MessageVerifier.class_7646.BROKEN_CHAIN;
			} else if (!this.verify(messageHeader, messageSignatureData, bs)) {
				this.precedingSignature = null;
				return MessageVerifier.class_7646.NOT_SECURE;
			} else {
				this.precedingSignature = messageSignatureData;
				return MessageVerifier.class_7646.SECURE;
			}
		}

		@Override
		public MessageVerifier.class_7646 storeHeaderVerification(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
			return this.method_45048(header, signature, bodyDigest);
		}

		@Override
		public MessageVerifier.class_7646 verify(SignedMessage message) {
			byte[] bs = message.signedBody().digest().asBytes();
			return this.method_45048(message.signedHeader(), message.headerSignature(), bs);
		}
	}

	public static enum class_7646 {
		SECURE,
		NOT_SECURE,
		BROKEN_CHAIN;
	}
}
