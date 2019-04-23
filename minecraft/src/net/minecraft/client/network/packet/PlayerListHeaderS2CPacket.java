package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class PlayerListHeaderS2CPacket implements Packet<ClientPlayPacketListener> {
	private Component header;
	private Component footer;

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.header = packetByteBuf.readTextComponent();
		this.footer = packetByteBuf.readTextComponent();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeTextComponent(this.header);
		packetByteBuf.writeTextComponent(this.footer);
	}

	public void method_11907(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerListHeader(this);
	}

	@Environment(EnvType.CLIENT)
	public Component getHeader() {
		return this.header;
	}

	@Environment(EnvType.CLIENT)
	public Component getFooter() {
		return this.footer;
	}
}
