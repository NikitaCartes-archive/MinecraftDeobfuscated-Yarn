package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class QueryBlockNbtC2SPacket implements Packet<ServerPlayPacketListener> {
	private int transactionId;
	private BlockPos pos;

	public QueryBlockNbtC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public QueryBlockNbtC2SPacket(int i, BlockPos blockPos) {
		this.transactionId = i;
		this.pos = blockPos;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.transactionId = packetByteBuf.readVarInt();
		this.pos = packetByteBuf.readBlockPos();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.transactionId);
		packetByteBuf.writeBlockPos(this.pos);
	}

	public void method_12095(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onQueryBlockNbt(this);
	}

	public int getTransactionId() {
		return this.transactionId;
	}

	public BlockPos getPos() {
		return this.pos;
	}
}
