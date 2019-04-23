package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginDisconnectS2CPacket implements Packet<ClientLoginPacketListener> {
	private Component reason;

	public LoginDisconnectS2CPacket() {
	}

	public LoginDisconnectS2CPacket(Component component) {
		this.reason = component;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.reason = Component.Serializer.fromLenientJsonString(packetByteBuf.readString(262144));
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeTextComponent(this.reason);
	}

	public void method_12637(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onDisconnect(this);
	}

	@Environment(EnvType.CLIENT)
	public Component getReason() {
		return this.reason;
	}
}
