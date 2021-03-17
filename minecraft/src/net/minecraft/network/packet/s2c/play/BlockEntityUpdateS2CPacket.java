package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class BlockEntityUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final BlockPos pos;
	private final int blockEntityType;
	private final NbtCompound tag;

	public BlockEntityUpdateS2CPacket(BlockPos pos, int blockEntityType, NbtCompound tag) {
		this.pos = pos;
		this.blockEntityType = blockEntityType;
		this.tag = tag;
	}

	public BlockEntityUpdateS2CPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.blockEntityType = buf.readUnsignedByte();
		this.tag = buf.readCompound();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeByte((byte)this.blockEntityType);
		buf.writeCompound(this.tag);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockEntityUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getPos() {
		return this.pos;
	}

	@Environment(EnvType.CLIENT)
	public int getBlockEntityType() {
		return this.blockEntityType;
	}

	@Environment(EnvType.CLIENT)
	public NbtCompound getCompoundTag() {
		return this.tag;
	}
}
