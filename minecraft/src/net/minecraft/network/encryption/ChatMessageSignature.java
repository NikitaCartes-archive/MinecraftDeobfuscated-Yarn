package net.minecraft.network.encryption;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.Signature;
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
	 * 
	 * @throws SignatureException when verifying fails
	 * 
	 * @param message the message to verify
	 */
	public boolean verify(Signature signature, Text message) throws SignatureException {
		if (this.canVerify()) {
			updateSignature(signature, message, this.sender, this.timestamp, this.saltSignature.salt());
			return signature.verify(this.saltSignature.signature());
		} else {
			return false;
		}
	}

	/**
	 * {@return whether {@code message} can be verified with this signature}
	 * 
	 * @throws SignatureException when verifying fails
	 * 
	 * @param message the message to verify
	 */
	public boolean verify(Signature signature, String message) throws SignatureException {
		return this.verify(signature, Text.literal(message));
	}

	/**
	 * Updates {@code signature} with the passed parameters.
	 * 
	 * @implNote The data to be signed is {@code salt}, followed by big-endian ordered
	 * {@code uuid}, followed by {@code time} as seconds from the UTC epoch, followed by
	 * UTF-8 encoded {@code message} bytes.
	 * 
	 * @throws SignatureException when updating signature fails
	 * 
	 * @see ChatMessageSigner#sign
	 * @see #verify
	 */
	public static void updateSignature(Signature signature, Text message, UUID sender, Instant time, long salt) throws SignatureException {
		byte[] bs = toByteArray(message);
		int i = 32 + bs.length;
		ByteBuffer byteBuffer = ByteBuffer.allocate(i).order(ByteOrder.BIG_ENDIAN);
		byteBuffer.putLong(salt);
		byteBuffer.putLong(sender.getMostSignificantBits()).putLong(sender.getLeastSignificantBits());
		byteBuffer.putLong(time.getEpochSecond());
		byteBuffer.put(bs);
		signature.update(byteBuffer.flip());
	}

	private static byte[] toByteArray(Text message) {
		String string = Text.Serializer.toSortedJsonString(message);
		return string.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * {@return whether the signature can be verified}
	 * 
	 * <p>Verifiable signature is not the same as verified signature. Signatures are verifiable
	 * if it has proper sender UUID and signature data. However, they can still fail to verify.
	 */
	public boolean canVerify() {
		return this.sender != Util.NIL_UUID && this.saltSignature.isSignaturePresent();
	}
}
