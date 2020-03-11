package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class PlayerListHeaderS2CPacket implements Packet<ClientPlayPacketListener> {
	private Text header;
	private Text footer;

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.header = buf.readText();
		this.footer = buf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeText(this.header);
		buf.writeText(this.footer);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerListHeader(this);
	}

	@Environment(EnvType.CLIENT)
	public Text getHeader() {
		return this.header;
	}

	@Environment(EnvType.CLIENT)
	public Text getFooter() {
		return this.footer;
	}
}
