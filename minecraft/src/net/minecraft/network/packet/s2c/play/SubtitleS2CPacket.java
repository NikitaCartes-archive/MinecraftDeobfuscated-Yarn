package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;

public class SubtitleS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, SubtitleS2CPacket> CODEC = Packet.createCodec(SubtitleS2CPacket::write, SubtitleS2CPacket::new);
	private final Text subtitle;

	public SubtitleS2CPacket(Text subtitle) {
		this.subtitle = subtitle;
	}

	private SubtitleS2CPacket(PacketByteBuf buf) {
		this.subtitle = buf.readUnlimitedText();
	}

	private void write(PacketByteBuf buf) {
		buf.writeText(this.subtitle);
	}

	@Override
	public PacketType<SubtitleS2CPacket> getPacketId() {
		return PlayPackets.SET_SUBTITLE_TEXT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSubtitle(this);
	}

	public Text getSubtitle() {
		return this.subtitle;
	}
}
