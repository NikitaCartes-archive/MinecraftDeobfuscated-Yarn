package net.minecraft;

import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.CookiePackets;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;

public class class_9099 {
	public static final class_9127<ServerLoginPacketListener> field_48247 = class_9147.method_56451(
		NetworkState.LOGIN,
		arg -> arg.method_56454(LoginPackets.HELLO_C2S, LoginHelloC2SPacket.CODEC)
				.method_56454(LoginPackets.KEY, LoginKeyC2SPacket.CODEC)
				.method_56454(LoginPackets.CUSTOM_QUERY_ANSWER, LoginQueryResponseC2SPacket.CODEC)
				.method_56454(LoginPackets.LOGIN_ACKNOWLEDGED, EnterConfigurationC2SPacket.CODEC)
				.method_56454(CookiePackets.COOKIE_RESPONSE, CookieResponseC2SPacket.CODEC)
	);
	public static final class_9127<ClientLoginPacketListener> field_48248 = class_9147.method_56455(
		NetworkState.LOGIN,
		arg -> arg.method_56454(LoginPackets.LOGIN_DISCONNECT, LoginDisconnectS2CPacket.CODEC)
				.method_56454(LoginPackets.HELLO_S2C, LoginHelloS2CPacket.CODEC)
				.method_56454(LoginPackets.GAME_PROFILE, LoginSuccessS2CPacket.CODEC)
				.method_56454(LoginPackets.LOGIN_COMPRESSION, LoginCompressionS2CPacket.CODEC)
				.method_56454(LoginPackets.CUSTOM_QUERY, LoginQueryRequestS2CPacket.CODEC)
				.method_56454(CookiePackets.COOKIE_REQUEST, CookieRequestS2CPacket.CODEC)
	);
}
