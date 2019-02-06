package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class WorldEventS2CPacket implements Packet<ClientPlayPacketListener> {
	private int eventId;
	private BlockPos pos;
	private int data;
	private boolean global;

	public WorldEventS2CPacket() {
	}

	public WorldEventS2CPacket(int i, BlockPos blockPos, int j, boolean bl) {
		this.eventId = i;
		this.pos = blockPos.toImmutable();
		this.data = j;
		this.global = bl;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.eventId = packetByteBuf.readInt();
		this.pos = packetByteBuf.readBlockPos();
		this.data = packetByteBuf.readInt();
		this.global = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeInt(this.eventId);
		packetByteBuf.writeBlockPos(this.pos);
		packetByteBuf.writeInt(this.data);
		packetByteBuf.writeBoolean(this.global);
	}

	public void method_11535(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11098(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean isGlobal() {
		return this.global;
	}

	@Environment(EnvType.CLIENT)
	public int getEventId() {
		return this.eventId;
	}

	@Environment(EnvType.CLIENT)
	public int getEffectData() {
		return this.data;
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getPos() {
		return this.pos;
	}
}
