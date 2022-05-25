package net.minecraft.network.encryption;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

/**
 * A signature for chat messages, consisting of the sender, the timestamp, and the signature data.
 */
public record ChatMessageSignature(UUID sender, Instant timestamp, NetworkEncryptionUtils.SignatureData saltSignature) {
	public static ChatMessageSignature none() {
		return new ChatMessageSignature(Util.NIL_UUID, Instant.now(), NetworkEncryptionUtils.SignatureData.NONE);
	}

	/**
	 * {@return whether {@code message} can be verified with this signature}
	 */
	public boolean verify(SignatureVerifier verifier, Text message) {
		return this.canVerify()
			? verifier.validate(updater -> updateSignature(updater, message, this.sender, this.timestamp, this.saltSignature.salt()), this.saltSignature.signature())
			: false;
	}

	/**
	 * {@return whether {@code message} can be verified with this signature}
	 * 
	 * @throws SignatureException when verifying fails
	 * 
	 * @param message the message to verify
	 */
	public boolean verify(SignatureVerifier verifier, String message) throws SignatureException {
		return this.verify(verifier, Text.literal(message));
	}

	/**
	 * Updates {@code updater} with the passed parameters.
	 * 
	 * @implNote The data to be signed is {@code salt}, followed by big-endian ordered
	 * {@code uuid}, followed by {@code time} as seconds from the UTC epoch, followed by
	 * UTF-8 encoded {@code message} bytes.
	 * 
	 * @throws SignatureException when updating signature fails
	 * 
	 * @see ChatMessageSigner#sign(net.minecraft.network.encryption.Signer, Text)
	 * @see #verify
	 */
	public static void updateSignature(SignatureUpdatable.SignatureUpdater updater, Text message, UUID sender, Instant time, long salt) throws SignatureException {
		byte[] bs = new byte[32];
		ByteBuffer byteBuffer = ByteBuffer.wrap(bs).order(ByteOrder.BIG_ENDIAN);
		byteBuffer.putLong(salt);
		byteBuffer.putLong(sender.getMostSignificantBits()).putLong(sender.getLeastSignificantBits());
		byteBuffer.putLong(time.getEpochSecond());
		updater.update(bs);
		updater.update(toByteArray(message));
	}

	private static byte[] toByteArray(Text message) {
		String string = Text.Serializer.toSortedJsonString(message);
		return string.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * {@return whether the signature can be verified}
	 * 
	 * <p>Verifiable signature is not the same as verified signature. A signatures is verifiable
	 * if it has proper sender UUID and signature data. However, it can still fail to verify.
	 */
	public boolean canVerify() {
		return this.sender != Util.NIL_UUID && this.saltSignature.isSignaturePresent();
	}

	/**
	 * {@return whether the message is from {@code expectedSender} and the signature
	 * can be verified}
	 * 
	 * <p>Verifiable signature is not the same as verified signature. A signatures is verifiable
	 * if it has proper sender UUID and signature data. However, it can still fail to verify.
	 */
	public boolean canVerifyFrom(UUID expectedSender) {
		return this.canVerify() && expectedSender.equals(this.sender);
	}
}
