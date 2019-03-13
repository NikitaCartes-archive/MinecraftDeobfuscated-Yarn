package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class LoginDisconnectS2CPacket implements Packet<ClientLoginPacketListener> {
	private TextComponent reason;

	public LoginDisconnectS2CPacket() {
	}

	public LoginDisconnectS2CPacket(TextComponent textComponent) {
		this.reason = textComponent;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.reason = TextComponent.Serializer.fromLenientJsonString(packetByteBuf.readString(262144));
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.method_10805(this.reason);
	}

	public void method_12637(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.method_12584(this);
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getReason() {
		return this.reason;
	}
}
