package net.minecraft.network.packet.s2c.login;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public class LoginDisconnectS2CPacket implements Packet<ClientLoginPacketListener> {
	private final Text reason;

	public LoginDisconnectS2CPacket(Text reason) {
		this.reason = reason;
	}

	public LoginDisconnectS2CPacket(PacketByteBuf buf) {
		this.reason = Text.Serializer.fromLenientJson(buf.readString(PacketByteBuf.MAX_TEXT_LENGTH));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.reason);
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onDisconnect(this);
	}

	public Text getReason() {
		return this.reason;
	}
}
