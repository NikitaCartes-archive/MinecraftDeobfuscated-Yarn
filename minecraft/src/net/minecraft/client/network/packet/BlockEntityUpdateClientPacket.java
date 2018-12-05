package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class BlockEntityUpdateClientPacket implements Packet<ClientPlayPacketListener> {
	private BlockPos pos;
	private int actionId;
	private CompoundTag tag;

	public BlockEntityUpdateClientPacket() {
	}

	public BlockEntityUpdateClientPacket(BlockPos blockPos, int i, CompoundTag compoundTag) {
		this.pos = blockPos;
		this.actionId = i;
		this.tag = compoundTag;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.pos = packetByteBuf.readBlockPos();
		this.actionId = packetByteBuf.readUnsignedByte();
		this.tag = packetByteBuf.readCompoundTag();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeByte((byte)this.actionId);
		packetByteBuf.writeCompoundTag(this.tag);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onBlockEntityUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getPos() {
		return this.pos;
	}

	@Environment(EnvType.CLIENT)
	public int getActionId() {
		return this.actionId;
	}

	@Environment(EnvType.CLIENT)
	public CompoundTag getCompoundTag() {
		return this.tag;
	}
}
