package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class KeepAliveClientPacket implements Packet<ClientPlayPacketListener> {
	private long field_12211;

	public KeepAliveClientPacket() {
	}

	public KeepAliveClientPacket(long l) {
		this.field_12211 = l;
	}

	public void method_11518(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onKeepAlive(this);
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
