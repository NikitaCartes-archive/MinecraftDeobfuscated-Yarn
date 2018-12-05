package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2868 implements Packet<ServerPlayPacketListener> {
	private int field_13052;

	public class_2868() {
	}

	@Environment(EnvType.CLIENT)
	public class_2868(int i) {
		this.field_13052 = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13052 = packetByteBuf.readShort();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeShort(this.field_13052);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12056(this);
	}

	public int method_12442() {
		return this.field_13052;
	}
}
