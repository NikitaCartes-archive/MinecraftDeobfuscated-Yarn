package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class KeepAliveServerPacket implements Packet<ServerPlayPacketListener> {
	private long id;

	public KeepAliveServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public KeepAliveServerPacket(long l) {
		this.id = l;
	}

	public void method_12266(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onKeepAlive(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readLong();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeLong(this.id);
	}

	public long getId() {
		return this.id;
	}
}
