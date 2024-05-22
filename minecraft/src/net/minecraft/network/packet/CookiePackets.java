package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ClientCookieRequestPacketListener;
import net.minecraft.network.listener.ServerCookieResponsePacketListener;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.util.Identifier;

public class CookiePackets {
	public static final PacketType<CookieRequestS2CPacket> COOKIE_REQUEST = s2c("cookie_request");
	public static final PacketType<CookieResponseC2SPacket> COOKIE_RESPONSE = c2s("cookie_response");

	private static <T extends Packet<ClientCookieRequestPacketListener>> PacketType<T> s2c(String id) {
		return new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.ofVanilla(id));
	}

	private static <T extends Packet<ServerCookieResponsePacketListener>> PacketType<T> c2s(String id) {
		return new PacketType<>(NetworkSide.SERVERBOUND, Identifier.ofVanilla(id));
	}
}
