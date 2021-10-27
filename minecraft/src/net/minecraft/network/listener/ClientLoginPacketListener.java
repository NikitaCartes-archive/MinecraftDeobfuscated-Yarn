package net.minecraft.network.listener;

import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;

public interface ClientLoginPacketListener extends PacketListener {
	void onHello(LoginHelloS2CPacket packet);

	void onSuccess(LoginSuccessS2CPacket packet);

	void onDisconnect(LoginDisconnectS2CPacket packet);

	void onCompression(LoginCompressionS2CPacket packet);

	void onQueryRequest(LoginQueryRequestS2CPacket packet);
}
