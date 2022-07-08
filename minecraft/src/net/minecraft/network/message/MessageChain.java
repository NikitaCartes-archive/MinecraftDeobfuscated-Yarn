package net.minecraft.network.message;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.encryption.Signer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.text.Text;

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

	private MessageChain.Signature pack(Signer signer, MessageMetadata metadata, Text content) {
		MessageSignatureData messageSignatureData = sign(signer, metadata, this.precedingSignature, content);
		this.precedingSignature = messageSignatureData;
		return new MessageChain.Signature(messageSignatureData);
	}

	private static MessageSignatureData sign(Signer signer, MessageMetadata metadata, @Nullable MessageSignatureData precedingSignature, Text content) {
		MessageHeader messageHeader = new MessageHeader(precedingSignature, metadata.sender());
		MessageBody messageBody = new MessageBody(content, metadata.timestamp(), metadata.salt(), List.of());
		byte[] bs = messageBody.digest().asBytes();
		return new MessageSignatureData(signer.sign(updatable -> messageHeader.update(updatable, bs)));
	}

	private SignedMessage unpack(MessageChain.Signature signature, MessageMetadata metadata, Text content) {
		SignedMessage signedMessage = createMessage(signature, this.precedingSignature, metadata, content);
		this.precedingSignature = signature.signature;
		return signedMessage;
	}

	private static SignedMessage createMessage(
		MessageChain.Signature signature, @Nullable MessageSignatureData precedingSignature, MessageMetadata metadata, Text content
	) {
		MessageHeader messageHeader = new MessageHeader(precedingSignature, metadata.sender());
		MessageBody messageBody = new MessageBody(content, metadata.timestamp(), metadata.salt(), List.of());
		return new SignedMessage(messageHeader, signature.signature, messageBody, Optional.empty());
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
		MessageChain.Signature pack(Signer signer, MessageMetadata metadata, Text content);
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
		MessageChain.Unpacker UNSIGNED = (signature, metadata, content) -> SignedMessage.ofUnsigned(metadata, content);

		SignedMessage unpack(MessageChain.Signature signature, MessageMetadata metadata, Text content);

		default FilteredMessage<SignedMessage> unpack(MessageChain.Signature signature, MessageMetadata metadata, FilteredMessage<Text> content) {
			return this.unpack(signature, metadata, content.raw()).withFilteredContent(content.filtered());
		}
	}
}
