package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.math.BlockPos;

public class BlockBreakingProgressS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, BlockBreakingProgressS2CPacket> CODEC = Packet.createCodec(
		BlockBreakingProgressS2CPacket::write, BlockBreakingProgressS2CPacket::new
	);
	private final int entityId;
	private final BlockPos pos;
	private final int progress;

	public BlockBreakingProgressS2CPacket(int entityId, BlockPos pos, int progress) {
		this.entityId = entityId;
		this.pos = pos;
		this.progress = progress;
	}

	private BlockBreakingProgressS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.pos = buf.readBlockPos();
		this.progress = buf.readUnsignedByte();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeBlockPos(this.pos);
		buf.writeByte(this.progress);
	}

	@Override
	public PacketType<BlockBreakingProgressS2CPacket> getPacketId() {
		return PlayPackets.BLOCK_DESTRUCTION;
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
