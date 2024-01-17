package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.util.Identifier;

public class HandshakePackets {
	public static final PacketIdentifier<HandshakeC2SPacket> INTENTION = c2s("intention");

	private static <T extends Packet<ServerHandshakePacketListener>> PacketIdentifier<T> c2s(String id) {
		return new PacketIdentifier<>(NetworkSide.SERVERBOUND, new Identifier(id));
	}
}
