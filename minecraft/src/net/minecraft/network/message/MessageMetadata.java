package net.minecraft.network.message;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.util.Util;

/**
 * A metadata for messages.
 */
public record MessageMetadata(UUID sender, Instant timestamp, long salt) {
	public MessageMetadata(PacketByteBuf buf) {
		this(buf.readUuid(), buf.readInstant(), buf.readLong());
	}

	/**
	 * {@return a new metadata with the given sender, current timestamp, and random salt}
	 */
	public static MessageMetadata of(UUID sender) {
		return new MessageMetadata(sender, Instant.now(), NetworkEncryptionUtils.SecureRandomUtil.nextLong());
	}

	/**
	 * {@return a new metadata without sender}
	 */
	public static MessageMetadata of() {
		return of(Util.NIL_UUID);
	}

	public void write(PacketByteBuf buf) {
		buf.writeUuid(this.sender);
		buf.writeInstant(this.timestamp);
		buf.writeLong(this.salt);
	}

	/**
	 * {@return {@code true} if the metadata does not have a sender's UUID}
	 */
	public boolean lacksSender() {
		return this.sender.equals(Util.NIL_UUID);
	}
}
