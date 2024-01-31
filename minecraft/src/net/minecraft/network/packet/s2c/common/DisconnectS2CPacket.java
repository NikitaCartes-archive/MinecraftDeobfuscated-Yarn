package net.minecraft.network.packet.s2c.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record DisconnectS2CPacket(Text reason) implements Packet<ClientCommonPacketListener> {
	public static final PacketCodec<ByteBuf, DisconnectS2CPacket> CODEC = TextCodecs.PACKET_CODEC.xmap(DisconnectS2CPacket::new, DisconnectS2CPacket::reason);

	@Override
	public PacketType<DisconnectS2CPacket> getPacketId() {
		return CommonPackets.DISCONNECT;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onDisconnect(this);
	}
}
