package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;

public class QueryBlockNbtC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int transactionId;
	private final BlockPos pos;

	public QueryBlockNbtC2SPacket(int transactionId, BlockPos pos) {
		this.transactionId = transactionId;
		this.pos = pos;
	}

	public QueryBlockNbtC2SPacket(PacketByteBuf buf) {
		this.transactionId = buf.readVarInt();
		this.pos = buf.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.transactionId);
		buf.writeBlockPos(this.pos);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onQueryBlockNbt(this);
	}

	public int getTransactionId() {
		return this.transactionId;
	}

	public BlockPos getPos() {
		return this.pos;
	}
}
