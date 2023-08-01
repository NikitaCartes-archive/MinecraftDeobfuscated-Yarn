package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public class DisconnectS2CPacket implements Packet<ClientCommonPacketListener> {
	private final Text reason;

	public DisconnectS2CPacket(Text reason) {
		this.reason = reason;
	}

	public DisconnectS2CPacket(PacketByteBuf buf) {
		this.reason = buf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.reason);
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onDisconnect(this);
	}

	public Text getReason() {
		return this.reason;
	}
}
