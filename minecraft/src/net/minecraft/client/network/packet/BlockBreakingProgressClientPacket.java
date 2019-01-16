package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class BlockBreakingProgressClientPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private BlockPos pos;
	private int progress;

	public BlockBreakingProgressClientPacket() {
	}

	public BlockBreakingProgressClientPacket(int i, BlockPos blockPos, int j) {
		this.entityId = i;
		this.pos = blockPos;
		this.progress = j;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityId = packetByteBuf.readVarInt();
		this.pos = packetByteBuf.readBlockPos();
		this.progress = packetByteBuf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeByte(this.progress);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockDestroyProgress(this);
	}

	@Environment(EnvType.CLIENT)
	public int getEntityId() {
		return this.entityId;
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getPos() {
		return this.pos;
	}

	@Environment(EnvType.CLIENT)
	public int getProgress() {
		return this.progress;
	}
}
