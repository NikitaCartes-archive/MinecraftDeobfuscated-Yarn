package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;

public class TitleS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, TitleS2CPacket> CODEC = Packet.createCodec(TitleS2CPacket::write, TitleS2CPacket::new);
	private final Text title;

	public TitleS2CPacket(Text title) {
		this.title = title;
	}

	private TitleS2CPacket(PacketByteBuf buf) {
		this.title = buf.readUnlimitedText();
	}

	private void write(PacketByteBuf buf) {
		buf.writeText(this.title);
	}

	@Override
	public PacketType<TitleS2CPacket> getPacketId() {
		return PlayPackets.SET_TITLE_TEXT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTitle(this);
	}

	public Text getTitle() {
		return this.title;
	}
}
