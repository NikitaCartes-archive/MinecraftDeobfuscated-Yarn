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

public class BlockActionS2CPacket implements Packet<ClientPlayPacketListener> {
	private BlockPos pos;
	private int type;
	private int data;
	private Block block;

	public BlockActionS2CPacket() {
	}

	public BlockActionS2CPacket(BlockPos blockPos, Block block, int i, int j) {
		this.pos = blockPos;
		this.block = block;
		this.type = i;
		this.data = j;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.type = packetByteBuf.readUnsignedByte();
		this.data = packetByteBuf.readUnsignedByte();
		this.block = Registry.BLOCK.get(packetByteBuf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeByte(this.type);
		packetByteBuf.writeByte(this.data);
		packetByteBuf.writeVarInt(Registry.BLOCK.getRawId(this.block));
	}

	public void method_11297(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11158(this);
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getPos() {
		return this.pos;
	}

	@Environment(EnvType.CLIENT)
	public int getType() {
		return this.type;
	}

	@Environment(EnvType.CLIENT)
	public int getData() {
		return this.data;
	}

	@Environment(EnvType.CLIENT)
	public Block getBlock() {
		return this.block;
	}
}
