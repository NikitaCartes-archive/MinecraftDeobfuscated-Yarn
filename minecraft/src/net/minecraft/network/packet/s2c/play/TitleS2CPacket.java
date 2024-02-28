package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record TitleS2CPacket(Text text) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, TitleS2CPacket> CODEC = PacketCodec.tuple(
		TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, TitleS2CPacket::text, TitleS2CPacket::new
	);

	@Override
	public PacketType<TitleS2CPacket> getPacketId() {
		return PlayPackets.SET_TITLE_TEXT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTitle(this);
	}
}
