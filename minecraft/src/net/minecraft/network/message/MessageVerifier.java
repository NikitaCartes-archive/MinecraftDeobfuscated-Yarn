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
	static MessageVerifier create(@Nullable PlayerPublicKey publicKey, boolean bl) {
		return (MessageVerifier)(publicKey != null ? new MessageVerifier.Impl(publicKey.createSignatureInstance()) : new MessageVerifier.class_7651(bl));
	}

	/**
	 * {@return the status of verifying the header}
	 * 
	 * <p>Clients can receive only the message header instead of the whole message. This
	 * allows the verification of such messages.
	 */
	MessageVerifier.Status verify(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest);

	/**
	 * {@return the status of verifying the message}
	 */
	MessageVerifier.Status verify(SignedMessage message);

	public static class Impl implements MessageVerifier {
		private final SignatureVerifier signatureVerifier;
		@Nullable
		private MessageSignatureData precedingSignature;
		private boolean lastMessageVerified = true;

		public Impl(SignatureVerifier signatureVerifier) {
			this.signatureVerifier = signatureVerifier;
		}

		private boolean verifyPrecedingSignature(MessageHeader header, MessageSignatureData messageSignatureData, boolean bl) {
			if (messageSignatureData.isEmpty()) {
				return false;
			} else {
				return bl && messageSignatureData.equals(this.precedingSignature)
					? true
					: this.precedingSignature == null || this.precedingSignature.equals(header.precedingSignature());
			}
		}

		private boolean verifyInternal(MessageHeader header, MessageSignatureData messageSignatureData, byte[] bodyDigest, boolean bl) {
			return this.verifyPrecedingSignature(header, messageSignatureData, bl) && messageSignatureData.verify(this.signatureVerifier, header, bodyDigest);
		}

		private MessageVerifier.Status getStatus(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest, boolean bl) {
			this.lastMessageVerified = this.lastMessageVerified && this.verifyInternal(header, signature, bodyDigest, bl);
			if (!this.lastMessageVerified) {
				return MessageVerifier.Status.BROKEN_CHAIN;
			} else {
				this.precedingSignature = signature;
				return MessageVerifier.Status.SECURE;
			}
		}

		@Override
		public MessageVerifier.Status verify(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
			return this.getStatus(header, signature, bodyDigest, false);
		}

		@Override
		public MessageVerifier.Status verify(SignedMessage message) {
			byte[] bs = message.signedBody().digest().asBytes();
			return this.getStatus(message.signedHeader(), message.headerSignature(), bs, true);
		}
	}

	/**
	 * The verification status of a message.
	 */
	public static enum Status {
		/**
		 * The message is verified.
		 */
		SECURE,
		/**
		 * The message cannot be verified.
		 */
		NOT_SECURE,
		/**
		 * The message cannot be verified due to the last message not being verified.
		 */
		BROKEN_CHAIN;
	}

	public static class class_7651 implements MessageVerifier {
		private final boolean field_39952;

		public class_7651(boolean bl) {
			this.field_39952 = bl;
		}

		private MessageVerifier.Status method_45102(MessageSignatureData messageSignatureData) {
			if (!messageSignatureData.isEmpty()) {
				return MessageVerifier.Status.BROKEN_CHAIN;
			} else {
				return this.field_39952 ? MessageVerifier.Status.BROKEN_CHAIN : MessageVerifier.Status.NOT_SECURE;
			}
		}

		@Override
		public MessageVerifier.Status verify(MessageHeader header, MessageSignatureData signature, byte[] bodyDigest) {
			return this.method_45102(signature);
		}

		@Override
		public MessageVerifier.Status verify(SignedMessage message) {
			return this.method_45102(message.headerSignature());
		}
	}
}
