package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;

public class PlayerListHeaderS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, PlayerListHeaderS2CPacket> CODEC = Packet.createCodec(
		PlayerListHeaderS2CPacket::write, PlayerListHeaderS2CPacket::new
	);
	private final Text header;
	private final Text footer;

	public PlayerListHeaderS2CPacket(Text header, Text footer) {
		this.header = header;
		this.footer = footer;
	}

	private PlayerListHeaderS2CPacket(PacketByteBuf buf) {
		this.header = buf.readUnlimitedText();
		this.footer = buf.readUnlimitedText();
	}

	private void write(PacketByteBuf buf) {
		buf.writeText(this.header);
		buf.writeText(this.footer);
	}

	@Override
	public PacketIdentifier<PlayerListHeaderS2CPacket> getPacketId() {
		return PlayPackets.TAB_LIST;
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
