package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class GuiCloseC2SPacket implements Packet<ServerPlayPacketListener> {
	private int id;

	public GuiCloseC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public GuiCloseC2SPacket(int id) {
		this.id = id;
	}

	public void method_12198(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onGuiClose(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.id);
	}
}
