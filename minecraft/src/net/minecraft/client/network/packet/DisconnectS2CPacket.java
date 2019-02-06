package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class DisconnectS2CPacket implements Packet<ClientPlayPacketListener> {
	private TextComponent reason;

	public DisconnectS2CPacket() {
	}

	public DisconnectS2CPacket(TextComponent textComponent) {
		this.reason = textComponent;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.reason = packetByteBuf.readTextComponent();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeTextComponent(this.reason);
	}

	public void method_11467(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11083(this);
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getReason() {
		return this.reason;
	}
}
