package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GuiCloseS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;

	public GuiCloseS2CPacket() {
	}

	public GuiCloseS2CPacket(int i) {
		this.id = i;
	}

	public void method_11427(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGuiClose(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.id);
	}
}
