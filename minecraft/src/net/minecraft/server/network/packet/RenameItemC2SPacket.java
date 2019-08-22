package net.minecraft.server.network.packet;

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
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.itemName = packetByteBuf.readString(32767);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.itemName);
	}

	public void method_12408(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onRenameItem(this);
	}

	public String getItemName() {
		return this.itemName;
	}
}
