package net.minecraft.network.packet.s2c.play;

import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockEventS2CPacket implements Packet<ClientPlayPacketListener> {
	private final BlockPos pos;
	private final int type;
	private final int data;
	private final Block block;

	public BlockEventS2CPacket(BlockPos pos, Block block, int type, int data) {
		this.pos = pos;
		this.block = block;
		this.type = type;
		this.data = data;
	}

	public BlockEventS2CPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.type = buf.readUnsignedByte();
		this.data = buf.readUnsignedByte();
		this.block = Registry.BLOCK.get(buf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeByte(this.type);
		buf.writeByte(this.data);
		buf.writeVarInt(Registry.BLOCK.getRawId(this.block));
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockEvent(this);
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public int getType() {
		return this.type;
	}

	public int getData() {
		return this.data;
	}

	public Block getBlock() {
		return this.block;
	}
}
