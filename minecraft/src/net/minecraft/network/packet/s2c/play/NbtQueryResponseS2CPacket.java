package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class NbtQueryResponseS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int transactionId;
	@Nullable
	private final NbtCompound nbt;

	public NbtQueryResponseS2CPacket(int transactionId, @Nullable NbtCompound nbt) {
		this.transactionId = transactionId;
		this.nbt = nbt;
	}

	public NbtQueryResponseS2CPacket(PacketByteBuf buf) {
		this.transactionId = buf.readVarInt();
		this.nbt = buf.readNbt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.transactionId);
		buf.writeNbt(this.nbt);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTagQuery(this);
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
