package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class PlayerListHeaderS2CPacket implements Packet<ClientPlayPacketListener> {
	private TextComponent header;
	private TextComponent footer;

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
		clientPlayPacketListener.method_11105(this);
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getHeader() {
		return this.header;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getFooter() {
		return this.footer;
	}
}
