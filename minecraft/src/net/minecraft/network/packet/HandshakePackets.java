package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ServerHandshakePacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.util.Identifier;

public class HandshakePackets {
	public static final PacketType<HandshakeC2SPacket> INTENTION = c2s("intention");

	private static <T extends Packet<ServerHandshakePacketListener>> PacketType<T> c2s(String id) {
		return new PacketType<>(NetworkSide.SERVERBOUND, Identifier.ofVanilla(id));
	}
}
