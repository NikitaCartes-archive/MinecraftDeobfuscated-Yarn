package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class BlockBreakingProgressS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int entityId;
	private final BlockPos pos;
	private final int progress;

	public BlockBreakingProgressS2CPacket(int entityId, BlockPos pos, int progress) {
		this.entityId = entityId;
		this.pos = pos;
		this.progress = progress;
	}

	public BlockBreakingProgressS2CPacket(PacketByteBuf packetByteBuf) {
		this.entityId = packetByteBuf.readVarInt();
		this.pos = packetByteBuf.readBlockPos();
		this.progress = packetByteBuf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeBlockPos(this.pos);
		buf.writeByte(this.progress);
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
