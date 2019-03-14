package net.minecraft.network.listener;

import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.server.network.packet.LoginKeyC2SPacket;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;

public interface ServerLoginPacketListener extends PacketListener {
	void onHello(LoginHelloC2SPacket loginHelloC2SPacket);

	void onKey(LoginKeyC2SPacket loginKeyC2SPacket);

	void onQueryResponse(LoginQueryResponseC2SPacket loginQueryResponseC2SPacket);
}
