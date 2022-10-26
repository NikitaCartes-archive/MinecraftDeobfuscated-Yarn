package net.minecraft.network.message;

import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.SignatureUpdatable;

/**
 * A list of messages a client has seen.
 */
public record LastSeenMessageList(List<MessageSignatureData> entries) {
	public static final Codec<LastSeenMessageList> CODEC = MessageSignatureData.CODEC.listOf().xmap(LastSeenMessageList::new, LastSeenMessageList::entries);
	public static LastSeenMessageList EMPTY = new LastSeenMessageList(List.of());
	public static final int MAX_ENTRIES = 20;

	public void updateSignatures(SignatureUpdatable.SignatureUpdater updater) throws SignatureException {
		updater.update(Ints.toByteArray(this.entries.size()));

		for (MessageSignatureData messageSignatureData : this.entries) {
			updater.update(messageSignatureData.data());
		}
	}

	public LastSeenMessageList.Indexed pack(MessageSignatureStorage storage) {
		return new LastSeenMessageList.Indexed(this.entries.stream().map(signature -> signature.pack(storage)).toList());
	}

	/**
	 * A record of messages acknowledged by a client.
	 * 
	 * <p>This holds the messages the client has recently seen, as well as the last
	 * message they received, if any.
	 */
	public static record Acknowledgment(int offset, BitSet acknowledged) {
		public Acknowledgment(PacketByteBuf buf) {
			this(buf.readVarInt(), buf.readBitSet(20));
		}

		public void write(PacketByteBuf buf) {
			buf.writeVarInt(this.offset);
			buf.writeBitSet(this.acknowledged, 20);
		}
	}

	public static record Indexed(List<MessageSignatureData.Indexed> buf) {
		public static final LastSeenMessageList.Indexed EMPTY = new LastSeenMessageList.Indexed(List.of());

		public Indexed(PacketByteBuf buf) {
			this(buf.readCollection(PacketByteBuf.getMaxValidator(ArrayList::new, 20), MessageSignatureData.Indexed::fromBuf));
		}

		public void write(PacketByteBuf buf) {
			buf.writeCollection(this.buf, MessageSignatureData.Indexed::write);
		}

		public Optional<LastSeenMessageList> unpack(MessageSignatureStorage storage) {
			List<MessageSignatureData> list = new ArrayList(this.buf.size());

			for (MessageSignatureData.Indexed indexed : this.buf) {
				Optional<MessageSignatureData> optional = indexed.getSignature(storage);
				if (optional.isEmpty()) {
					return Optional.empty();
				}

				list.add((MessageSignatureData)optional.get());
			}

			return Optional.of(new LastSeenMessageList(list));
		}
	}
}
