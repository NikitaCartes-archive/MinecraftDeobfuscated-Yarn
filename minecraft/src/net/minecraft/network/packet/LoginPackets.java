package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.util.Identifier;

public class LoginPackets {
	public static final PacketIdentifier<LoginQueryRequestS2CPacket> CUSTOM_QUERY = s2c("custom_query");
	public static final PacketIdentifier<LoginSuccessS2CPacket> GAME_PROFILE = s2c("game_profile");
	public static final PacketIdentifier<LoginHelloS2CPacket> HELLO_S2C = s2c("hello");
	public static final PacketIdentifier<LoginCompressionS2CPacket> LOGIN_COMPRESSION = s2c("login_compression");
	public static final PacketIdentifier<LoginDisconnectS2CPacket> LOGIN_DISCONNECT = s2c("login_disconnect");
	public static final PacketIdentifier<LoginQueryResponseC2SPacket> CUSTOM_QUERY_ANSWER = c2s("custom_query_answer");
	public static final PacketIdentifier<LoginHelloC2SPacket> HELLO_C2S = c2s("hello");
	public static final PacketIdentifier<LoginKeyC2SPacket> KEY = c2s("key");
	public static final PacketIdentifier<EnterConfigurationC2SPacket> LOGIN_ACKNOWLEDGED = c2s("login_acknowledged");

	private static <T extends Packet<ClientLoginPacketListener>> PacketIdentifier<T> s2c(String id) {
		return new PacketIdentifier<>(NetworkSide.CLIENTBOUND, new Identifier(id));
	}

	private static <T extends Packet<ServerLoginPacketListener>> PacketIdentifier<T> c2s(String id) {
		return new PacketIdentifier<>(NetworkSide.SERVERBOUND, new Identifier(id));
	}
}
