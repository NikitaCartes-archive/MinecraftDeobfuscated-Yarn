package net.minecraft.network.message;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * A signed message, consisting of the signature, the signed content,
 * and the optional unsigned content supplied when the chat decorator produced
 * unsigned message due to the chat preview being disabled on either side.
 * 
 * <p>Note that the signature itself might not be valid.
 */
public record SignedMessage(MessageHeader signedHeader, MessageSignatureData headerSignature, MessageBody signedBody, Optional<Text> unsignedContent) {
	public static final Duration SERVERBOUND_TIME_TO_LIVE = Duration.ofMinutes(5L);
	public static final Duration CLIENTBOUND_TIME_TO_LIVE = SERVERBOUND_TIME_TO_LIVE.plus(Duration.ofMinutes(2L));

	public SignedMessage(PacketByteBuf buf) {
		this(new MessageHeader(buf), new MessageSignatureData(buf), new MessageBody(buf), buf.readOptional(PacketByteBuf::readText));
	}

	public static SignedMessage method_45041(DecoratedContents decoratedContents) {
		MessageMetadata messageMetadata = MessageMetadata.of();
		MessageBody messageBody = new MessageBody(decoratedContents, messageMetadata.timestamp(), messageMetadata.salt(), LastSeenMessageList.EMPTY);
		MessageHeader messageHeader = new MessageHeader(null, messageMetadata.sender());
		return new SignedMessage(messageHeader, MessageSignatureData.EMPTY, messageBody, Optional.empty());
	}

	public void write(PacketByteBuf buf) {
		this.signedHeader.write(buf);
		this.headerSignature.write(buf);
		this.signedBody.write(buf);
		buf.writeOptional(this.unsignedContent, PacketByteBuf::writeText);
	}

	public FilteredMessage<SignedMessage> withFilteredContent(FilteredMessage<DecoratedContents> filteredMessage) {
		return filteredMessage.method_45000(
			this,
			decoratedContents -> this.getSignedContent().equals(decoratedContents)
					? this
					: new SignedMessage(this.signedHeader, this.headerSignature, this.signedBody.method_45047(decoratedContents), this.unsignedContent)
		);
	}

	public SignedMessage withUnsignedContent(Text unsignedContent) {
		Optional<Text> optional = !this.getSignedContent().decorated().equals(unsignedContent) ? Optional.of(unsignedContent) : Optional.empty();
		return new SignedMessage(this.signedHeader, this.headerSignature, this.signedBody, optional);
	}

	/**
	 * {@return the signed chat message with {@link #unsignedContent} removed if it exists}
	 * 
	 * @implNote This returns itself if the message does not have an unsigned content.
	 */
	public SignedMessage withoutUnsigned() {
		return this.unsignedContent.isPresent() ? new SignedMessage(this.signedHeader, this.headerSignature, this.signedBody, Optional.empty()) : this;
	}

	public boolean verify(SignatureVerifier verifier) {
		return this.headerSignature.verify(verifier, this.signedHeader, this.signedBody);
	}

	/**
	 * {@return whether the message can be verified using the public key}
	 */
	public boolean verify(PlayerPublicKey key) {
		SignatureVerifier signatureVerifier = key.createSignatureInstance();
		return this.verify(signatureVerifier);
	}

	/**
	 * {@return whether the message can be verified using the public key <strong>or if the
	 * player does not have the key</strong>}
	 */
	public boolean verify(MessageSourceProfile profile) {
		PlayerPublicKey playerPublicKey = profile.playerPublicKey();
		return playerPublicKey != null && this.verify(playerPublicKey);
	}

	public DecoratedContents getSignedContent() {
		return this.signedBody.content();
	}

	/**
	 * {@return the content of the message}
	 * 
	 * <p>This returns the unsigned content if present, and fallbacks to the signed content.
	 */
	public Text getContent() {
		return (Text)this.unsignedContent().orElse(this.getSignedContent().decorated());
	}

	public Instant getTimestamp() {
		return this.signedBody.timestamp();
	}

	public long getSalt() {
		return this.signedBody.salt();
	}

	public boolean isExpiredOnServer(Instant currentTime) {
		return currentTime.isAfter(this.getTimestamp().plus(SERVERBOUND_TIME_TO_LIVE));
	}

	public boolean isExpiredOnClient(Instant currentTime) {
		return currentTime.isAfter(this.getTimestamp().plus(CLIENTBOUND_TIME_TO_LIVE));
	}

	public MessageMetadata createMetadata() {
		return new MessageMetadata(this.signedHeader.sender(), this.getTimestamp(), this.getSalt());
	}

	@Nullable
	public LastSeenMessageList.Entry toLastSeenMessageEntry() {
		MessageMetadata messageMetadata = this.createMetadata();
		return !this.headerSignature.isEmpty() && !messageMetadata.lacksSender()
			? new LastSeenMessageList.Entry(messageMetadata.sender(), this.headerSignature)
			: null;
	}

	public boolean method_45040(ServerPlayerEntity serverPlayerEntity) {
		return !this.headerSignature.isEmpty() && this.signedHeader.sender().equals(serverPlayerEntity.getUuid());
	}
}
