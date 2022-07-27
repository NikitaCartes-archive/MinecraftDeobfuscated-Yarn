package net.minecraft.network.message;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.encryption.Signer;

/**
 * A class for handling the "message chain".
 * 
 * <p>{@link MessageHeader} includes the signature of the last message the client has seen.
 * This can be used to verify the legitimacy of a chain of messages, since if the chain
 * is valid, the last message's  "previous signature" should be able to verify the preceding
 * message.
 * 
 * <p>Clients signing a message with its preceding message's signature is called
 * "packing", and the server creating a signed message with its preceding message's
 * signature is called "unpacking". Unpacked messages can then be verified to check the
 * chain's legitimacy.
 */
public class MessageChain {
	@Nullable
	private MessageSignatureData precedingSignature;

	private MessageChain.Signature pack(Signer signer, MessageMetadata metadata, DecoratedContents contents, LastSeenMessageList lastSeenMessages) {
		MessageSignatureData messageSignatureData = sign(signer, metadata, this.precedingSignature, contents, lastSeenMessages);
		this.precedingSignature = messageSignatureData;
		return new MessageChain.Signature(messageSignatureData);
	}

	private static MessageSignatureData sign(
		Signer signer, MessageMetadata metadata, @Nullable MessageSignatureData precedingSignature, DecoratedContents contents, LastSeenMessageList lastSeenMessages
	) {
		MessageHeader messageHeader = new MessageHeader(precedingSignature, metadata.sender());
		MessageBody messageBody = new MessageBody(contents, metadata.timestamp(), metadata.salt(), lastSeenMessages);
		byte[] bs = messageBody.digest().asBytes();
		return new MessageSignatureData(signer.sign(updatable -> messageHeader.update(updatable, bs)));
	}

	private SignedMessage unpack(MessageChain.Signature signature, MessageMetadata metadata, DecoratedContents contents, LastSeenMessageList lastSeenMessages) {
		SignedMessage signedMessage = createMessage(signature, this.precedingSignature, metadata, contents, lastSeenMessages);
		this.precedingSignature = signature.signature;
		return signedMessage;
	}

	private static SignedMessage createMessage(
		MessageChain.Signature signature,
		@Nullable MessageSignatureData precedingSignature,
		MessageMetadata metadata,
		DecoratedContents contents,
		LastSeenMessageList lastSeenMessage
	) {
		MessageHeader messageHeader = new MessageHeader(precedingSignature, metadata.sender());
		MessageBody messageBody = new MessageBody(contents, metadata.timestamp(), metadata.salt(), lastSeenMessage);
		return new SignedMessage(messageHeader, signature.signature, messageBody, Optional.empty(), FilterMask.PASS_THROUGH);
	}

	public MessageChain.Unpacker getUnpacker() {
		return this::unpack;
	}

	public MessageChain.Packer getPacker() {
		return this::pack;
	}

	/**
	 * Packers sign a message on the client with its preceding message's signature.
	 * 
	 * @see MessageChain#getPacker
	 */
	@FunctionalInterface
	public interface Packer {
		MessageChain.Signature pack(Signer signer, MessageMetadata metadata, DecoratedContents contents, LastSeenMessageList lastSeenMessages);
	}

	public static record Signature(MessageSignatureData signature) {
	}

	/**
	 * Unpacker creates a signed message on the server with the server's preceding message
	 * signature when they receive a message. Unpacked messages can then be verified to check
	 * the message chain's legitimacy.
	 * 
	 * <p>Messages must be unpacked in the order of the message's reception, as it affects
	 * the resulting signed message.
	 * 
	 * @see MessageChain#getUnpacker
	 */
	@FunctionalInterface
	public interface Unpacker {
		MessageChain.Unpacker UNSIGNED = (signature, metadata, content, lastSeenMessages) -> SignedMessage.ofUnsigned(metadata, content);

		SignedMessage unpack(MessageChain.Signature signature, MessageMetadata metadata, DecoratedContents content, LastSeenMessageList lastSeenMessages);
	}
}
