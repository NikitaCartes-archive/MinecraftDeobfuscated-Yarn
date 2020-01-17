package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class KeepAliveC2SPacket implements Packet<ServerPlayPacketListener> {
	private long id;

	public KeepAliveC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public KeepAliveC2SPacket(long id) {
		this.id = id;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onKeepAlive(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeLong(this.id);
	}

	public long getId() {
		return this.id;
	}
}
