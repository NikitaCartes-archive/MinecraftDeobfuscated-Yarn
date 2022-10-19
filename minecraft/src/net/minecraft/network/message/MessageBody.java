package net.minecraft.network.message;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;
import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.SignatureUpdatable;

/**
 * A body of a message, including the content, timestamp, salt used for the digest
 * (the hashed body), and the list of players' "last seen messages".
 * Other bits of information, such as sender, are included directly in the packet.
 */
public record MessageBody(String content, Instant timestamp, long salt, LastSeenMessageList lastSeenMessages) {
	public static MessageBody ofUnsigned(String content) {
		return new MessageBody(content, Instant.now(), 0L, LastSeenMessageList.EMPTY);
	}

	public void update(SignatureUpdatable.SignatureUpdater updater) throws SignatureException {
		updater.update(Longs.toByteArray(this.salt));
		updater.update(Longs.toByteArray(this.timestamp.getEpochSecond()));
		byte[] bs = this.content.getBytes(StandardCharsets.UTF_8);
		updater.update(Ints.toByteArray(bs.length));
		updater.update(bs);
		this.lastSeenMessages.updateSignatures(updater);
	}

	public MessageBody.Serialized toSerialized(MessageSignatureData.Packer packer) {
		return new MessageBody.Serialized(this.content, this.timestamp, this.salt, this.lastSeenMessages.pack(packer));
	}

	/**
	 * The serialized version of {@code MessageBody} used in networking.
	 * 
	 * @see MessageBody#toSerialized
	 */
	public static record Serialized(String content, Instant timestamp, long salt, LastSeenMessageList.Indexed lastSeen) {
		public Serialized(PacketByteBuf buf) {
			this(buf.readString(256), buf.readInstant(), buf.readLong(), new LastSeenMessageList.Indexed(buf));
		}

		public void write(PacketByteBuf buf) {
			buf.writeString(this.content, 256);
			buf.writeInstant(this.timestamp);
			buf.writeLong(this.salt);
			this.lastSeen.write(buf);
		}

		public Optional<MessageBody> toBody(MessageSignatureData.Unpacker unpacker) {
			return this.lastSeen.unpack(unpacker).map(lastSeenMessages -> new MessageBody(this.content, this.timestamp, this.salt, lastSeenMessages));
		}
	}
}
