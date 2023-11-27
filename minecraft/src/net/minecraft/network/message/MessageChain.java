package net.minecraft.network.message;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.encryption.Signer;
import net.minecraft.text.Text;
import net.minecraft.util.TextifiedException;
import org.slf4j.Logger;

/**
 * A class for handling the "message chain".
 * 
 * <p>A message chain (since 1.19.3) is implemented using an integer that is incremented
 * for each message, called "index". {@link MessageLink} represents the link that a
 * particular message has.
 * 
 * <p>Clients signing a message with its preceding message's index is called
 * "packing", and the server creating a signed message with its preceding message's
 * index is called "unpacking". Unpacked messages can then be verified to check the
 * chain's legitimacy.
 * 
 * @see MessageLink
 */
public class MessageChain {
	private static final Logger LOGGER = LogUtils.getLogger();
	@Nullable
	private MessageLink link;
	private Instant lastTimestamp = Instant.EPOCH;

	public MessageChain(UUID sender, UUID sessionId) {
		this.link = MessageLink.of(sender, sessionId);
	}

	public MessageChain.Packer getPacker(Signer signer) {
		return body -> {
			MessageLink messageLink = this.nextLink();
			return messageLink == null ? null : new MessageSignatureData(signer.sign(updatable -> SignedMessage.update(updatable, messageLink, body)));
		};
	}

	public MessageChain.Unpacker getUnpacker(PlayerPublicKey playerPublicKey) {
		SignatureVerifier signatureVerifier = playerPublicKey.createSignatureInstance();
		return (signature, body) -> {
			MessageLink messageLink = this.nextLink();
			if (messageLink == null) {
				throw new MessageChain.MessageChainException(Text.translatable("chat.disabled.chain_broken"), false);
			} else if (playerPublicKey.data().isExpired()) {
				throw new MessageChain.MessageChainException(Text.translatable("chat.disabled.expiredProfileKey"), false);
			} else if (body.timestamp().isBefore(this.lastTimestamp)) {
				throw new MessageChain.MessageChainException(Text.translatable("multiplayer.disconnect.out_of_order_chat"), true);
			} else {
				this.lastTimestamp = body.timestamp();
				SignedMessage signedMessage = new SignedMessage(messageLink, signature, body, null, FilterMask.PASS_THROUGH);
				if (!signedMessage.verify(signatureVerifier)) {
					throw new MessageChain.MessageChainException(Text.translatable("multiplayer.disconnect.unsigned_chat"), true);
				} else {
					if (signedMessage.isExpiredOnServer(Instant.now())) {
						LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", body.content());
					}

					return signedMessage;
				}
			}
		};
	}

	@Nullable
	private MessageLink nextLink() {
		MessageLink messageLink = this.link;
		if (messageLink != null) {
			this.link = messageLink.next();
		}

		return messageLink;
	}

	public static class MessageChainException extends TextifiedException {
		private final boolean shouldDisconnect;

		public MessageChainException(Text message, boolean shouldDisconnect) {
			super(message);
			this.shouldDisconnect = shouldDisconnect;
		}

		public boolean shouldDisconnect() {
			return this.shouldDisconnect;
		}
	}

	/**
	 * Packers sign a message on the client with its preceding message's index.
	 * 
	 * @see MessageChain#getPacker
	 */
	@FunctionalInterface
	public interface Packer {
		MessageChain.Packer NONE = body -> null;

		@Nullable
		MessageSignatureData pack(MessageBody body);
	}

	/**
	 * Unpacker creates a signed message on the server with the server's preceding message
	 * index when they receive a message. Unpacked messages can then be verified to check
	 * the message chain's legitimacy.
	 * 
	 * @see MessageChain#getUnpacker
	 */
	@FunctionalInterface
	public interface Unpacker {
		static MessageChain.Unpacker unsigned(UUID sender, BooleanSupplier secureProfileEnforced) {
			return (signature, body) -> {
				if (secureProfileEnforced.getAsBoolean()) {
					throw new MessageChain.MessageChainException(Text.translatable("chat.disabled.missingProfileKey"), false);
				} else {
					return SignedMessage.ofUnsigned(sender, body.content());
				}
			};
		}

		SignedMessage unpack(@Nullable MessageSignatureData signature, MessageBody body) throws MessageChain.MessageChainException;
	}
}
