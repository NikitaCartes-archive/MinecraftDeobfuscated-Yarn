package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class KeepAliveS2CPacket implements Packet<ClientPlayPacketListener> {
	private long id;

	public KeepAliveS2CPacket() {
	}

	public KeepAliveS2CPacket(long id) {
		this.id = id;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onKeepAlive(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readLong();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeLong(this.id);
	}

	@Environment(EnvType.CLIENT)
	public long getId() {
		return this.id;
	}
}
