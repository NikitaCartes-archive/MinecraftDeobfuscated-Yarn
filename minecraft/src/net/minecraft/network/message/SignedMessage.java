package net.minecraft.network.message;

import com.google.common.primitives.Ints;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.SignatureException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.encryption.SignatureUpdatable;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

/**
 * A signed message, consisting of the signature, the signed content,
 * the message body, the link to its preceding message, and the optional
 * unsigned content supplied when the message decorator modified the message.
 * 
 * <p>Note that the signature itself might not be valid.
 */
public record SignedMessage(
	MessageLink link, @Nullable MessageSignatureData signature, MessageBody signedBody, @Nullable Text unsignedContent, FilterMask filterMask
) {
	public static final MapCodec<SignedMessage> field_40846 = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					MessageLink.field_40849.fieldOf("link").forGetter(SignedMessage::link),
					MessageSignatureData.CODEC.optionalFieldOf("signature").forGetter(signedMessage -> Optional.ofNullable(signedMessage.signature)),
					MessageBody.CODEC.forGetter(SignedMessage::signedBody),
					Codecs.TEXT.optionalFieldOf("unsigned_content").forGetter(signedMessage -> Optional.ofNullable(signedMessage.unsignedContent)),
					FilterMask.CODEC.optionalFieldOf("filter_mask", FilterMask.PASS_THROUGH).forGetter(SignedMessage::filterMask)
				)
				.apply(
					instance,
					(messageLink, optional, messageBody, optional2, filterMask) -> new SignedMessage(
							messageLink, (MessageSignatureData)optional.orElse(null), messageBody, (Text)optional2.orElse(null), filterMask
						)
				)
	);
	private static final UUID NIL_UUID = Util.NIL_UUID;
	public static final Duration SERVERBOUND_TIME_TO_LIVE = Duration.ofMinutes(5L);
	public static final Duration CLIENTBOUND_TIME_TO_LIVE = SERVERBOUND_TIME_TO_LIVE.plus(Duration.ofMinutes(2L));

	/**
	 * {@return a new signed message with empty signature}
	 */
	public static SignedMessage ofUnsigned(String content) {
		return ofUnsigned(NIL_UUID, content);
	}

	/**
	 * {@return a new signed message with given sender UUID and empty signature}
	 */
	public static SignedMessage ofUnsigned(UUID sender, String content) {
		MessageBody messageBody = MessageBody.ofUnsigned(content);
		MessageLink messageLink = MessageLink.of(sender);
		return new SignedMessage(messageLink, null, messageBody, null, FilterMask.PASS_THROUGH);
	}

	public SignedMessage withUnsignedContent(Text unsignedContent) {
		Text text = !unsignedContent.equals(Text.literal(this.getSignedContent())) ? unsignedContent : null;
		return new SignedMessage(this.link, this.signature, this.signedBody, text, this.filterMask);
	}

	/**
	 * {@return the signed chat message with {@link #unsignedContent} removed if it exists}
	 * 
	 * @implNote This returns itself if the message does not have an unsigned content.
	 */
	public SignedMessage withoutUnsigned() {
		return this.unsignedContent != null ? new SignedMessage(this.link, this.signature, this.signedBody, null, this.filterMask) : this;
	}

	/**
	 * {@return the signed chat message with {@code filterMask} added}
	 */
	public SignedMessage withFilterMask(FilterMask filterMask) {
		return this.filterMask.equals(filterMask) ? this : new SignedMessage(this.link, this.signature, this.signedBody, this.unsignedContent, filterMask);
	}

	/**
	 * {@return this signed chat message if {@code enabled} is {@code true},
	 * otherwise a new signed chat message without filtered parts}
	 */
	public SignedMessage withFilterMaskEnabled(boolean enabled) {
		return this.withFilterMask(enabled ? this.filterMask : FilterMask.PASS_THROUGH);
	}

	public static void update(SignatureUpdatable.SignatureUpdater updater, MessageLink link, MessageBody body) throws SignatureException {
		updater.update(Ints.toByteArray(1));
		link.update(updater);
		body.update(updater);
	}

	public boolean verify(SignatureVerifier verifier) {
		return this.signature != null && this.signature.verify(verifier, updater -> update(updater, this.link, this.signedBody));
	}

	public String getSignedContent() {
		return this.signedBody.content();
	}

	public Text getContent() {
		return (Text)Objects.requireNonNullElseGet(this.unsignedContent, () -> Text.literal(this.getSignedContent()));
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

	public UUID getSender() {
		return this.link.sender();
	}

	public boolean isSenderMissing() {
		return this.getSender().equals(NIL_UUID);
	}

	public boolean hasSignature() {
		return this.signature != null;
	}

	/**
	 * {@return whether the message can be verified as from {@code sender}}
	 * 
	 * <p>This does not actually verify that the message is, in fact, from {@code sender}.
	 * Rather, this returns whether it's possible to verify that {@code sender} sent this
	 * message.
	 */
	public boolean canVerifyFrom(UUID sender) {
		return this.hasSignature() && this.link.sender().equals(sender);
	}

	public boolean isFullyFiltered() {
		return this.filterMask.isFullyFiltered();
	}
}
