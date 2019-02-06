package net.minecraft.network.listener;

import net.minecraft.class_2913;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.server.network.packet.LoginKeyC2SPacket;

public interface ServerLoginPacketListener extends PacketListener {
	void method_12641(LoginHelloC2SPacket loginHelloC2SPacket);

	void method_12642(LoginKeyC2SPacket loginKeyC2SPacket);

	void method_12640(class_2913 arg);
}
