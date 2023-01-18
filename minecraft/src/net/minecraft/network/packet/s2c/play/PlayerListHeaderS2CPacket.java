package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public class PlayerListHeaderS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Text header;
	private final Text footer;

	public PlayerListHeaderS2CPacket(Text header, Text footer) {
		this.header = header;
		this.footer = footer;
	}

	public PlayerListHeaderS2CPacket(PacketByteBuf buf) {
		this.header = buf.readText();
		this.footer = buf.readText();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.header);
		buf.writeText(this.footer);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onPlayerListHeader(this);
	}

	public Text getHeader() {
		return this.header;
	}

	public Text getFooter() {
		return this.footer;
	}
}
