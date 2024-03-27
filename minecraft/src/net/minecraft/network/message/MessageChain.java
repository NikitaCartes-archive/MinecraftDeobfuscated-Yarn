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
	static final Logger LOGGER = LogUtils.getLogger();
	@Nullable
	MessageLink link;
	Instant lastTimestamp = Instant.EPOCH;

	public MessageChain(UUID sender, UUID sessionId) {
		this.link = MessageLink.of(sender, sessionId);
	}

	public MessageChain.Packer getPacker(Signer signer) {
		return body -> {
			MessageLink messageLink = this.link;
			if (messageLink == null) {
				return null;
			} else {
				this.link = messageLink.next();
				return new MessageSignatureData(signer.sign(updatable -> SignedMessage.update(updatable, messageLink, body)));
			}
		};
	}

	public MessageChain.Unpacker getUnpacker(PlayerPublicKey playerPublicKey) {
		final SignatureVerifier signatureVerifier = playerPublicKey.createSignatureInstance();
		return new MessageChain.Unpacker() {
			@Override
			public SignedMessage unpack(@Nullable MessageSignatureData messageSignatureData, MessageBody messageBody) throws MessageChain.MessageChainException {
				if (messageSignatureData == null) {
					throw new MessageChain.MessageChainException(MessageChain.MessageChainException.MISSING_PROFILE_KEY_EXCEPTION);
				} else if (playerPublicKey.data().isExpired()) {
					throw new MessageChain.MessageChainException(MessageChain.MessageChainException.EXPIRED_PROFILE_KEY_EXCEPTION);
				} else {
					MessageLink messageLink = MessageChain.this.link;
					if (messageLink == null) {
						throw new MessageChain.MessageChainException(MessageChain.MessageChainException.CHAIN_BROKEN_EXCEPTION);
					} else if (messageBody.timestamp().isBefore(MessageChain.this.lastTimestamp)) {
						this.setChainBroken();
						throw new MessageChain.MessageChainException(MessageChain.MessageChainException.OUT_OF_ORDER_CHAT_EXCEPTION);
					} else {
						MessageChain.this.lastTimestamp = messageBody.timestamp();
						SignedMessage signedMessage = new SignedMessage(messageLink, messageSignatureData, messageBody, null, FilterMask.PASS_THROUGH);
						if (!signedMessage.verify(signatureVerifier)) {
							this.setChainBroken();
							throw new MessageChain.MessageChainException(MessageChain.MessageChainException.INVALID_SIGNATURE_EXCEPTION);
						} else {
							if (signedMessage.isExpiredOnServer(Instant.now())) {
								MessageChain.LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", messageBody.content());
							}

							MessageChain.this.link = messageLink.next();
							return signedMessage;
						}
					}
				}
			}

			@Override
			public void setChainBroken() {
				MessageChain.this.link = null;
			}
		};
	}

	public static class MessageChainException extends TextifiedException {
		static final Text MISSING_PROFILE_KEY_EXCEPTION = Text.translatable("chat.disabled.missingProfileKey");
		static final Text CHAIN_BROKEN_EXCEPTION = Text.translatable("chat.disabled.chain_broken");
		static final Text EXPIRED_PROFILE_KEY_EXCEPTION = Text.translatable("chat.disabled.expiredProfileKey");
		static final Text INVALID_SIGNATURE_EXCEPTION = Text.translatable("chat.disabled.invalid_signature");
		static final Text OUT_OF_ORDER_CHAT_EXCEPTION = Text.translatable("chat.disabled.out_of_order_chat");

		public MessageChainException(Text message) {
			super(message);
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
					throw new MessageChain.MessageChainException(MessageChain.MessageChainException.MISSING_PROFILE_KEY_EXCEPTION);
				} else {
					return SignedMessage.ofUnsigned(sender, body.content());
				}
			};
		}

		SignedMessage unpack(@Nullable MessageSignatureData signature, MessageBody body) throws MessageChain.MessageChainException;

		default void setChainBroken() {
		}
	}
}
