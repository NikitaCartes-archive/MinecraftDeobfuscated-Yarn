package net.minecraft.network.listener;

import net.minecraft.class_2913;
import net.minecraft.server.network.packet.LoginKeyServerPacket;
import net.minecraft.server.packet.LoginHelloServerPacket;

public interface ServerLoginPacketListener extends PacketListener {
	void method_12641(LoginHelloServerPacket loginHelloServerPacket);

	void method_12642(LoginKeyServerPacket loginKeyServerPacket);

	void method_12640(class_2913 arg);
}
