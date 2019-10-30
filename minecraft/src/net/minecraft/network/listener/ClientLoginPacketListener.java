package net.minecraft.network.listener;

import net.minecraft.client.network.packet.LoginCompressionS2CPacket;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.client.network.packet.LoginHelloS2CPacket;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.client.network.packet.LoginSuccessS2CPacket;

public interface ClientLoginPacketListener extends PacketListener {
	void onHello(LoginHelloS2CPacket packet);

	void onLoginSuccess(LoginSuccessS2CPacket loginSuccessS2CPacket);

	void onDisconnect(LoginDisconnectS2CPacket packet);

	void onCompression(LoginCompressionS2CPacket packet);

	void onQueryRequest(LoginQueryRequestS2CPacket packet);
}
