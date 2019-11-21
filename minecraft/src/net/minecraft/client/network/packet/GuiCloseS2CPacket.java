package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GuiCloseS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;

	public GuiCloseS2CPacket() {
	}

	public GuiCloseS2CPacket(int id) {
		this.id = id;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGuiClose(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readUnsignedByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.id);
	}
}
