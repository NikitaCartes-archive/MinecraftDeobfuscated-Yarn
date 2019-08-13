package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ButtonClickC2SPacket implements Packet<ServerPlayPacketListener> {
	private int syncId;
	private int buttonId;

	public ButtonClickC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public ButtonClickC2SPacket(int i, int j) {
		this.syncId = i;
		this.buttonId = j;
	}

	public void method_12185(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onButtonClick(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readByte();
		this.buttonId = packetByteBuf.readByte();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.syncId);
		packetByteBuf.writeByte(this.buttonId);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getButtonId() {
		return this.buttonId;
	}
}
