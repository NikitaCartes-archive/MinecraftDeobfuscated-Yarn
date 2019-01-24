package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockActionClientPacket implements Packet<ClientPlayPacketListener> {
	private BlockPos pos;
	private int arg1;
	private int arg2;
	private Block block;

	public BlockActionClientPacket() {
	}

	public BlockActionClientPacket(BlockPos blockPos, Block block, int i, int j) {
		this.pos = blockPos;
		this.block = block;
		this.arg1 = i;
		this.arg2 = j;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.arg1 = packetByteBuf.readUnsignedByte();
		this.arg2 = packetByteBuf.readUnsignedByte();
		this.block = Registry.BLOCK.getInt(packetByteBuf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeByte(this.arg1);
		packetByteBuf.writeByte(this.arg2);
		packetByteBuf.writeVarInt(Registry.BLOCK.getRawId(this.block));
	}

	public void method_11297(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockAction(this);
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getPos() {
		return this.pos;
	}

	@Environment(EnvType.CLIENT)
	public int getArgumentFirst() {
		return this.arg1;
	}

	@Environment(EnvType.CLIENT)
	public int getArgumentSecond() {
		return this.arg2;
	}

	@Environment(EnvType.CLIENT)
	public Block getBlock() {
		return this.block;
	}
}
