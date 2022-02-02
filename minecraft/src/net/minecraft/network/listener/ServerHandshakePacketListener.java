package net.minecraft.network.listener;

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;

public interface ServerHandshakePacketListener extends ServerPacketListener {
	void onHandshake(HandshakeC2SPacket packet);
}
