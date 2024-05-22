package net.minecraft.network.packet.s2c.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.server.ServerLinks;

public record ServerLinksS2CPacket(ServerLinks links) implements Packet<ClientCommonPacketListener> {
	public static final PacketCodec<ByteBuf, ServerLinksS2CPacket> CODEC = PacketCodec.tuple(
		ServerLinks.CODEC, ServerLinksS2CPacket::links, ServerLinksS2CPacket::new
	);

	@Override
	public PacketType<ServerLinksS2CPacket> getPacketId() {
		return CommonPackets.SERVER_LINKS;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onServerLinks(this);
	}
}
