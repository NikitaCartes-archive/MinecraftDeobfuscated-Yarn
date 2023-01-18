package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
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

	public BlockBreakingProgressS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.pos = buf.readBlockPos();
		this.progress = buf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeBlockPos(this.pos);
		buf.writeByte(this.progress);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockBreakingProgress(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getProgress() {
		return this.progress;
	}
}
