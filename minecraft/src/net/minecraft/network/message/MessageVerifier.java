package net.minecraft.network.message;

import com.mojang.logging.LogUtils;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.network.encryption.SignatureVerifier;
import org.slf4j.Logger;

/**
 * Verifies incoming messages' signature and the message chain.
 * 
 * <p>Methods in this interface must be called in the order of the message's reception,
 * as it affects the verification result.
 */
@FunctionalInterface
public interface MessageVerifier {
	Logger LOGGER = LogUtils.getLogger();
	MessageVerifier NO_SIGNATURE = SignedMessage::stripSignature;
	MessageVerifier UNVERIFIED = message -> {
		LOGGER.error("Received chat message from {}, but they have no chat session initialized and secure chat is enforced", message.getSender());
		return null;
	};

	@Nullable
	SignedMessage ensureVerified(SignedMessage message);

	public static class Impl implements MessageVerifier {
		private final SignatureVerifier signatureVerifier;
		private final BooleanSupplier expirationChecker;
		@Nullable
		private SignedMessage lastVerifiedMessage;
		private boolean lastMessageVerified = true;

		public Impl(SignatureVerifier signatureVerifier, BooleanSupplier expirationChecker) {
			this.signatureVerifier = signatureVerifier;
			this.expirationChecker = expirationChecker;
		}

		private boolean verifyPrecedingSignature(SignedMessage message) {
			if (message.equals(this.lastVerifiedMessage)) {
				return true;
			} else if (this.lastVerifiedMessage != null && !message.link().linksTo(this.lastVerifiedMessage.link())) {
				LOGGER.error(
					"Received out-of-order chat message from {}: expected index > {} for session {}, but was {} for session {}",
					message.getSender(),
					this.lastVerifiedMessage.link().index(),
					this.lastVerifiedMessage.link().sessionId(),
					message.link().index(),
					message.link().sessionId()
				);
				return false;
			} else {
				return true;
			}
		}

		private boolean verify(SignedMessage message) {
			if (this.expirationChecker.getAsBoolean()) {
				LOGGER.error("Received message from player with expired profile public key: {}", message);
				return false;
			} else if (!message.verify(this.signatureVerifier)) {
				LOGGER.error("Received message with invalid signature from {}", message.getSender());
				return false;
			} else {
				return this.verifyPrecedingSignature(message);
			}
		}

		@Nullable
		@Override
		public SignedMessage ensureVerified(SignedMessage message) {
			this.lastMessageVerified = this.lastMessageVerified && this.verify(message);
			if (!this.lastMessageVerified) {
				return null;
			} else {
				this.lastVerifiedMessage = message;
				return message;
			}
		}
	}
}
