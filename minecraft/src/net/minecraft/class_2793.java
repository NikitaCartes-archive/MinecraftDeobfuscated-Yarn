package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2793 implements Packet<ServerPlayPacketListener> {
	private int field_12758;

	public class_2793() {
	}

	@Environment(EnvType.CLIENT)
	public class_2793(int i) {
		this.field_12758 = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12758 = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_12758);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12050(this);
	}

	public int method_12086() {
		return this.field_12758;
	}
}
