package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class ButtonClickC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int syncId;
	private final int buttonId;

	public ButtonClickC2SPacket(int syncId, int buttonId) {
		this.syncId = syncId;
		this.buttonId = buttonId;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onButtonClick(this);
	}

	public ButtonClickC2SPacket(PacketByteBuf buf) {
		this.syncId = buf.readByte();
		this.buttonId = buf.readByte();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeByte(this.buttonId);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getButtonId() {
		return this.buttonId;
	}
}
