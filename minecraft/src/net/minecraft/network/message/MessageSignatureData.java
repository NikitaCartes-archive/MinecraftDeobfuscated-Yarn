package net.minecraft.network.message;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.SignatureUpdatable;
import net.minecraft.network.encryption.SignatureVerifier;

/**
 * A message signature data that can be verified.
 */
public record MessageSignatureData(byte[] data) {
	public static final int SIZE = 256;

	public MessageSignatureData(byte[] data) {
		Preconditions.checkState(data.length == 256, "Invalid message signature size");
		this.data = data;
	}

	public static MessageSignatureData fromBuf(PacketByteBuf buf) {
		byte[] bs = new byte[256];
		buf.readBytes(bs);
		return new MessageSignatureData(bs);
	}

	public static void write(PacketByteBuf buf, MessageSignatureData signature) {
		buf.writeBytes(signature.data);
	}

	/**
	 * {@return whether the signature data is verified}
	 * 
	 * @param verifier the verifier that is created with the sender's public key
	 */
	public boolean verify(SignatureVerifier verifier, SignatureUpdatable updatable) {
		return verifier.validate(updatable, this.data);
	}

	/**
	 * {@return the byte buffer containing the signature data}
	 */
	public ByteBuffer toByteBuffer() {
		return ByteBuffer.wrap(this.data);
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
		return Base64.getEncoder().encodeToString(this.data);
	}

	public MessageSignatureData.Indexed pack(MessageSignatureData.Packer packer) {
		int i = packer.pack(this);
		return i != -1 ? new MessageSignatureData.Indexed(i) : new MessageSignatureData.Indexed(this);
	}

	public static record Indexed(int id, @Nullable MessageSignatureData fullSignature) {
		public static final int MISSING_ID = -1;

		public Indexed(MessageSignatureData signature) {
			this(-1, signature);
		}

		public Indexed(int id) {
			this(id, null);
		}

		public static MessageSignatureData.Indexed fromBuf(PacketByteBuf buf) {
			int i = buf.readVarInt() - 1;
			return i == -1 ? new MessageSignatureData.Indexed(MessageSignatureData.fromBuf(buf)) : new MessageSignatureData.Indexed(i);
		}

		public static void write(PacketByteBuf buf, MessageSignatureData.Indexed indexed) {
			buf.writeVarInt(indexed.id() + 1);
			if (indexed.fullSignature() != null) {
				MessageSignatureData.write(buf, indexed.fullSignature());
			}
		}

		public Optional<MessageSignatureData> getSignature(MessageSignatureData.Unpacker unpacker) {
			return this.fullSignature != null ? Optional.of(this.fullSignature) : Optional.ofNullable(unpacker.unpack(this.id));
		}
	}

	public interface Packer {
		int MISSING = -1;

		int pack(MessageSignatureData signature);
	}

	public interface Unpacker {
		@Nullable
		MessageSignatureData unpack(int index);
	}
}
