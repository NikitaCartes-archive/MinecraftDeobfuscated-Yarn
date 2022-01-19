package net.minecraft.network.listener;

import net.minecraft.class_6857;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;

public interface ServerLoginPacketListener extends class_6857 {
	void onHello(LoginHelloC2SPacket packet);

	void onKey(LoginKeyC2SPacket packet);

	void onQueryResponse(LoginQueryResponseC2SPacket packet);
}
