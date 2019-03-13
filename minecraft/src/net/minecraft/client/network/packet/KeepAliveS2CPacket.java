package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class KeepAliveS2CPacket implements Packet<ClientPlayPacketListener> {
	private long field_12211;

	public KeepAliveS2CPacket() {
	}

	public KeepAliveS2CPacket(long l) {
		this.field_12211 = l;
	}

	public void method_11518(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11147(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12211 = packetByteBuf.readLong();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeLong(this.field_12211);
	}

	@Environment(EnvType.CLIENT)
	public long method_11517() {
		return this.field_12211;
	}
}
