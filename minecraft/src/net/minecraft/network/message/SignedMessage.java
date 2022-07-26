package net.minecraft.network.message;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_7649;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.text.Text;

/**
 * A signed message, consisting of the signature, the signed content,
 * and the optional unsigned content supplied when the chat decorator produced
 * unsigned message due to the chat preview being disabled on either side.
 * 
 * <p>Note that the signature itself might not be valid.
 */
public record SignedMessage(
	MessageHeader signedHeader, MessageSignatureData headerSignature, MessageBody signedBody, Optional<Text> unsignedContent, class_7649 filterMask
) {
	public static final Duration SERVERBOUND_TIME_TO_LIVE = Duration.ofMinutes(5L);
	public static final Duration CLIENTBOUND_TIME_TO_LIVE = SERVERBOUND_TIME_TO_LIVE.plus(Duration.ofMinutes(2L));

	public SignedMessage(PacketByteBuf buf) {
		this(new MessageHeader(buf), new MessageSignatureData(buf), new MessageBody(buf), buf.readOptional(PacketByteBuf::readText), class_7649.method_45090(buf));
	}

	/**
	 * {@return a new signed message with empty signature}
	 */
	public static SignedMessage ofUnsigned(DecoratedContents content) {
		return method_45098(MessageMetadata.of(), content);
	}

	public static SignedMessage method_45098(MessageMetadata messageMetadata, DecoratedContents decoratedContents) {
		MessageBody messageBody = new MessageBody(decoratedContents, messageMetadata.timestamp(), messageMetadata.salt(), LastSeenMessageList.EMPTY);
		MessageHeader messageHeader = new MessageHeader(null, messageMetadata.sender());
		return new SignedMessage(messageHeader, MessageSignatureData.EMPTY, messageBody, Optional.empty(), class_7649.field_39942);
	}

	public void write(PacketByteBuf buf) {
		this.signedHeader.write(buf);
		this.headerSignature.write(buf);
		this.signedBody.write(buf);
		buf.writeOptional(this.unsignedContent, PacketByteBuf::writeText);
		class_7649.method_45091(buf, this.filterMask);
	}

	public SignedMessage withUnsignedContent(Text unsignedContent) {
		Optional<Text> optional = !this.getSignedContent().decorated().equals(unsignedContent) ? Optional.of(unsignedContent) : Optional.empty();
		return new SignedMessage(this.signedHeader, this.headerSignature, this.signedBody, optional, this.filterMask);
	}

	/**
	 * {@return the signed chat message with {@link #unsignedContent} removed if it exists}
	 * 
	 * @implNote This returns itself if the message does not have an unsigned content.
	 */
	public SignedMessage withoutUnsigned() {
		return this.unsignedContent.isPresent()
			? new SignedMessage(this.signedHeader, this.headerSignature, this.signedBody, Optional.empty(), this.filterMask)
			: this;
	}

	public SignedMessage method_45097(class_7649 arg) {
		return this.filterMask.equals(arg) ? this : new SignedMessage(this.signedHeader, this.headerSignature, this.signedBody, this.unsignedContent, arg);
	}

	public SignedMessage method_45099(boolean bl) {
		return this.method_45097(bl ? this.filterMask : class_7649.field_39942);
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

	/**
	 * {@return whether the message can be verified as from {@code sender}}
	 * 
	 * <p>This does not actually verify that the message is, in fact, from {@code sender}.
	 * Rather, this returns whether it's possible to verify that {@code sender} sent this
	 * message.
	 */
	public boolean canVerifyFrom(UUID uUID) {
		return !this.headerSignature.isEmpty() && this.signedHeader.sender().equals(uUID);
	}

	public boolean method_45100() {
		return this.filterMask.method_45093();
	}
}
