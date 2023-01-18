package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public class OverlayMessageS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Text message;

	public OverlayMessageS2CPacket(Text message) {
		this.message = message;
	}

	public OverlayMessageS2CPacket(PacketByteBuf buf) {
		this.message = buf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.message);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOverlayMessage(this);
	}

	public Text getMessage() {
		return this.message;
	}
}
