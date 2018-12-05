package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2827 implements Packet<ServerPlayPacketListener> {
	private long field_12883;

	public class_2827() {
	}

	@Environment(EnvType.CLIENT)
	public class_2827(long l) {
		this.field_12883 = l;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12082(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12883 = packetByteBuf.readLong();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeLong(this.field_12883);
	}

	public long method_12267() {
		return this.field_12883;
	}
}
