package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ClientPingResultPacketListener;
import net.minecraft.network.listener.ServerQueryPingPacketListener;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.util.Identifier;

public class PingPackets {
	public static final PacketIdentifier<PingResultS2CPacket> PONG_RESPONSE = s2c("pong_response");
	public static final PacketIdentifier<QueryPingC2SPacket> PING_REQUEST = c2s("ping_request");

	private static <T extends Packet<ClientPingResultPacketListener>> PacketIdentifier<T> s2c(String id) {
		return new PacketIdentifier<>(NetworkSide.CLIENTBOUND, new Identifier(id));
	}

	private static <T extends Packet<ServerQueryPingPacketListener>> PacketIdentifier<T> c2s(String id) {
		return new PacketIdentifier<>(NetworkSide.SERVERBOUND, new Identifier(id));
	}
}
