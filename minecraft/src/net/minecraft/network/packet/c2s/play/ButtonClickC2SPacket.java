package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ButtonClickC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ButtonClickC2SPacket> CODEC = Packet.createCodec(ButtonClickC2SPacket::write, ButtonClickC2SPacket::new);
	private final int syncId;
	private final int buttonId;

	public ButtonClickC2SPacket(int syncId, int buttonId) {
		this.syncId = syncId;
		this.buttonId = buttonId;
	}

	private ButtonClickC2SPacket(PacketByteBuf buf) {
		this.syncId = buf.readByte();
		this.buttonId = buf.readByte();
	}

	private void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeByte(this.buttonId);
	}

	@Override
	public PacketType<ButtonClickC2SPacket> getPacketId() {
		return PlayPackets.CONTAINER_BUTTON_CLICK;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onButtonClick(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getButtonId() {
		return this.buttonId;
	}
}
