package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GuiCloseC2SPacket implements Packet<ServerPlayPacketListener> {
	private int id;

	public GuiCloseC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public GuiCloseC2SPacket(int i) {
		this.id = i;
	}

	public void method_12198(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onGuiClose(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.id = packetByteBuf.readByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.id);
	}
}
