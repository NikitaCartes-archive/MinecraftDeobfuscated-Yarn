package net.minecraft.network.listener;

import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.server.network.packet.LoginKeyC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;

public interface ServerLoginPacketListener extends PacketListener {
	void method_12641(LoginHelloC2SPacket loginHelloC2SPacket);

	void method_12642(LoginKeyC2SPacket loginKeyC2SPacket);

	void method_12640(LoginQueryResponseC2SPacket loginQueryResponseC2SPacket);
}
