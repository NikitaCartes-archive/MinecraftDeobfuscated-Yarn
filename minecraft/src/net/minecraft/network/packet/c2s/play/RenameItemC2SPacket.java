package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class RenameItemC2SPacket implements Packet<ServerPlayPacketListener> {
	private String itemName;

	public RenameItemC2SPacket() {
	}

	public RenameItemC2SPacket(String string) {
		this.itemName = string;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.itemName = buf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.itemName);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onRenameItem(this);
	}

	public String getItemName() {
		return this.itemName;
	}
}
