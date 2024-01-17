package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;

public class NbtQueryResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, NbtQueryResponseS2CPacket> CODEC = Packet.createCodec(
		NbtQueryResponseS2CPacket::write, NbtQueryResponseS2CPacket::new
	);
	private final int transactionId;
	@Nullable
	private final NbtCompound nbt;

	public NbtQueryResponseS2CPacket(int transactionId, @Nullable NbtCompound nbt) {
		this.transactionId = transactionId;
		this.nbt = nbt;
	}

	private NbtQueryResponseS2CPacket(PacketByteBuf buf) {
		this.transactionId = buf.readVarInt();
		this.nbt = buf.readNbt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.transactionId);
		buf.writeNbt(this.nbt);
	}

	@Override
	public PacketIdentifier<NbtQueryResponseS2CPacket> getPacketId() {
		return PlayPackets.TAG_QUERY;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onNbtQueryResponse(this);
	}

	public int getTransactionId() {
		return this.transactionId;
	}

	@Nullable
	public NbtCompound getNbt() {
		return this.nbt;
	}

	@Override
	public boolean isWritingErrorSkippable() {
		return true;
	}
}
