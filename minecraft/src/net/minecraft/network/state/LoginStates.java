package net.minecraft.network.state;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkState;
import net.minecraft.network.NetworkStateBuilder;
import net.minecraft.network.PacketByteBuf;
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

public class LoginStates {
	public static final NetworkState.Factory<ServerLoginPacketListener, PacketByteBuf> C2S_FACTORY = NetworkStateBuilder.c2s(
		NetworkPhase.LOGIN,
		builder -> builder.add(LoginPackets.HELLO_C2S, LoginHelloC2SPacket.CODEC)
				.add(LoginPackets.KEY, LoginKeyC2SPacket.CODEC)
				.add(LoginPackets.CUSTOM_QUERY_ANSWER, LoginQueryResponseC2SPacket.CODEC)
				.add(LoginPackets.LOGIN_ACKNOWLEDGED, EnterConfigurationC2SPacket.CODEC)
				.add(CookiePackets.COOKIE_RESPONSE, CookieResponseC2SPacket.CODEC)
	);
	public static final NetworkState<ServerLoginPacketListener> C2S = C2S_FACTORY.bind(PacketByteBuf::new);
	public static final NetworkState.Factory<ClientLoginPacketListener, PacketByteBuf> S2C_FACTORY = NetworkStateBuilder.s2c(
		NetworkPhase.LOGIN,
		builder -> builder.add(LoginPackets.LOGIN_DISCONNECT, LoginDisconnectS2CPacket.CODEC)
				.add(LoginPackets.HELLO_S2C, LoginHelloS2CPacket.CODEC)
				.add(LoginPackets.GAME_PROFILE, LoginSuccessS2CPacket.CODEC)
				.add(LoginPackets.LOGIN_COMPRESSION, LoginCompressionS2CPacket.CODEC)
				.add(LoginPackets.CUSTOM_QUERY, LoginQueryRequestS2CPacket.CODEC)
				.add(CookiePackets.COOKIE_REQUEST, CookieRequestS2CPacket.CODEC)
	);
	public static final NetworkState<ClientLoginPacketListener> S2C = S2C_FACTORY.bind(PacketByteBuf::new);
}
