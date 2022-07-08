package net.minecraft.network.message;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.util.Arrays;
import java.util.Base64;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.SignatureVerifier;

/**
 * A message signature data that can be verified when given the header.
 */
public record MessageSignatureData(byte[] data) {
	public static final MessageSignatureData EMPTY = new MessageSignatureData(ByteArrays.EMPTY_ARRAY);

	public MessageSignatureData(PacketByteBuf buf) {
		this(buf.readByteArray());
	}

	public void write(PacketByteBuf buf) {
		buf.writeByteArray(this.data);
	}

	/**
	 * {@return whether the signature data is verified}
	 * 
	 * @param verifier the verifier that is created with the sender's public key
	 */
	public boolean verify(SignatureVerifier verifier, MessageHeader header, MessageBody body) {
		if (!this.isEmpty()) {
			byte[] bs = body.digest().asBytes();
			return verifier.validate(updatable -> header.update(updatable, bs), this.data);
		} else {
			return false;
		}
	}

	/**
	 * {@return whether the signature data is verified}
	 * 
	 * @param verifier the verifier that is created with the sender's public key
	 * @param bodyDigest the {@linkplain MessageBody#digest digest of the message body}
	 */
	public boolean verify(SignatureVerifier verifier, MessageHeader header, byte[] bodyDigest) {
		return !this.isEmpty() ? verifier.validate(updatable -> header.update(updatable, bodyDigest), this.data) : false;
	}

	public boolean isEmpty() {
		return this.data.length == 0;
	}

	/**
	 * {@return the base64-encoded data, or {@code null} if the data is empty}
	 */
	@Nullable
	public String toStringOrNull() {
		return !this.isEmpty() ? Base64.getEncoder().encodeToString(this.data) : null;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof MessageSignatureData messageSignatureData && Arrays.equals(this.data, messageSignatureData.data)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.data);
	}

	public String toString() {
		return !this.isEmpty() ? Base64.getEncoder().encodeToString(this.data) : "empty";
	}
}
