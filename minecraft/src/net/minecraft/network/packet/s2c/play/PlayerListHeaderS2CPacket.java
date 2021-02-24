package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class PlayerListHeaderS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Text header;
	private final Text footer;

	public PlayerListHeaderS2CPacket(PacketByteBuf packetByteBuf) {
		this.header = packetByteBuf.readText();
		this.footer = packetByteBuf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) {
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
