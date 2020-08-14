package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class RenameItemC2SPacket implements Packet<ServerPlayPacketListener> {
	private String name;

	public RenameItemC2SPacket() {
	}

	public RenameItemC2SPacket(String name) {
		this.name = name;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.name = buf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.name);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onRenameItem(this);
	}

	public String getName() {
		return this.name;
	}
}
