package net.minecraft.network;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ButtonClickServerPacket implements Packet<ServerPlayPacketListener> {
	private int syncId;
	private int field_12812;

	public ButtonClickServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public ButtonClickServerPacket(int i, int j) {
		this.syncId = i;
		this.field_12812 = j;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12055(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readByte();
		this.field_12812 = packetByteBuf.readByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.syncId);
		packetByteBuf.writeByte(this.field_12812);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int method_12186() {
		return this.field_12812;
	}
}
