package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ClientCookieRequestPacketListener;
import net.minecraft.network.listener.ServerCookieResponsePacketListener;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.util.Identifier;

public class CookiePackets {
	public static final PacketIdentifier<CookieRequestS2CPacket> COOKIE_REQUEST = s2c("cookie_request");
	public static final PacketIdentifier<CookieResponseC2SPacket> COOKIE_RESPONSE = c2s("cookie_response");

	private static <T extends Packet<ClientCookieRequestPacketListener>> PacketIdentifier<T> s2c(String id) {
		return new PacketIdentifier<>(NetworkSide.CLIENTBOUND, new Identifier(id));
	}

	private static <T extends Packet<ServerCookieResponsePacketListener>> PacketIdentifier<T> c2s(String id) {
		return new PacketIdentifier<>(NetworkSide.SERVERBOUND, new Identifier(id));
	}
}
