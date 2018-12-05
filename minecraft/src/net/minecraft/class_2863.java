package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2863 implements Packet<ServerPlayPacketListener> {
	private int field_13036;

	public class_2863() {
	}

	@Environment(EnvType.CLIENT)
	public class_2863(int i) {
		this.field_13036 = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13036 = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_13036);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12080(this);
	}

	public int method_12431() {
		return this.field_13036;
	}
}
