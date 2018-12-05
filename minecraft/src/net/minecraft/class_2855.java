package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2855 implements Packet<ServerPlayPacketListener> {
	private String field_13013;

	public class_2855() {
	}

	public class_2855(String string) {
		this.field_13013 = string;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13013 = packetByteBuf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.field_13013);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12060(this);
	}

	public String method_12407() {
		return this.field_13013;
	}
}
