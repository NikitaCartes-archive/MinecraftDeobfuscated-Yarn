package net.minecraft.network.listener;

import net.minecraft.client.network.packet.LoginCompressionS2CPacket;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.client.network.packet.LoginHelloS2CPacket;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.client.network.packet.LoginSuccessS2CPacket;

public interface ClientLoginPacketListener extends PacketListener {
	void method_12587(LoginHelloS2CPacket loginHelloS2CPacket);

	void method_12588(LoginSuccessS2CPacket loginSuccessS2CPacket);

	void method_12584(LoginDisconnectS2CPacket loginDisconnectS2CPacket);

	void method_12585(LoginCompressionS2CPacket loginCompressionS2CPacket);

	void method_12586(LoginQueryRequestS2CPacket loginQueryRequestS2CPacket);
}
