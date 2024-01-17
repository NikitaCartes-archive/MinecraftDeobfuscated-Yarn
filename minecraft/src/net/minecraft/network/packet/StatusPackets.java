package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.util.Identifier;

public class StatusPackets {
	public static final PacketIdentifier<QueryResponseS2CPacket> STATUS_RESPONSE = s2c("status_response");
	public static final PacketIdentifier<QueryRequestC2SPacket> STATUS_REQUEST = c2s("status_request");

	private static <T extends Packet<ClientQueryPacketListener>> PacketIdentifier<T> s2c(String id) {
		return new PacketIdentifier<>(NetworkSide.CLIENTBOUND, new Identifier(id));
	}

	private static <T extends Packet<ServerQueryPacketListener>> PacketIdentifier<T> c2s(String id) {
		return new PacketIdentifier<>(NetworkSide.SERVERBOUND, new Identifier(id));
	}
}
