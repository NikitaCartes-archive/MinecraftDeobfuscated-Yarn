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

public record ChatMessageSignature(UUID sender, Instant timeStamp, NetworkEncryptionUtils.SignatureData saltSignature) {
	public static ChatMessageSignature none() {
		return new ChatMessageSignature(Util.NIL_UUID, Instant.now(), NetworkEncryptionUtils.SignatureData.NONE);
	}

	public boolean verify(Signature signature, Text message) throws SignatureException {
		updateSignature(signature, message, this.sender, this.timeStamp, this.saltSignature.salt());
		return signature.verify(this.saltSignature.signature());
	}

	public boolean verify(Signature signature, String message) throws SignatureException {
		return this.verify(signature, Text.literal(message));
	}

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
}
