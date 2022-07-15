package net.minecraft.network.message;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;

/**
 * A list of messages a client has seen.
 */
public record LastSeenMessageList(List<LastSeenMessageList.Entry> entries) {
	public static LastSeenMessageList EMPTY = new LastSeenMessageList(List.of());
	public static final int MAX_ENTRIES = 5;

	public LastSeenMessageList(PacketByteBuf buf) {
		this(buf.readCollection(PacketByteBuf.getMaxValidator(ArrayList::new, 5), LastSeenMessageList.Entry::new));
	}

	public void write(PacketByteBuf buf) {
		buf.writeCollection(this.entries, (buf2, entries) -> entries.write(buf2));
	}

	public void write(DataOutput output) throws IOException {
		for (LastSeenMessageList.Entry entry : this.entries) {
			UUID uUID = entry.profileId();
			MessageSignatureData messageSignatureData = entry.lastSignature();
			output.writeByte(70);
			output.writeLong(uUID.getMostSignificantBits());
			output.writeLong(uUID.getLeastSignificantBits());
			output.write(messageSignatureData.data());
		}
	}

	/**
	 * A record of messages acknowledged by a client.
	 * 
	 * <p>This holds the messages the client has recently seen, as well as the last
	 * message they received, if any.
	 */
	public static record Acknowledgment(LastSeenMessageList lastSeen, Optional<LastSeenMessageList.Entry> lastReceived) {
		public Acknowledgment(PacketByteBuf buf) {
			this(new LastSeenMessageList(buf), buf.readOptional(LastSeenMessageList.Entry::new));
		}

		public void write(PacketByteBuf buf) {
			this.lastSeen.write(buf);
			buf.writeOptional(this.lastReceived, (buf2, lastReceived) -> lastReceived.write(buf2));
		}
	}

	/**
	 * A pair of a player's UUID and the signature of the last message they saw,
	 * used as an entry of {@link LastSeenMessageList}.
	 */
	public static record Entry(UUID profileId, MessageSignatureData lastSignature) {
		public Entry(PacketByteBuf buf) {
			this(buf.readUuid(), new MessageSignatureData(buf));
		}

		public void write(PacketByteBuf buf) {
			buf.writeUuid(this.profileId);
			this.lastSignature.write(buf);
		}
	}
}
