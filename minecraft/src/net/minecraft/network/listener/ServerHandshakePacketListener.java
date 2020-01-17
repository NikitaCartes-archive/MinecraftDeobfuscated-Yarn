package net.minecraft.network.listener;

import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;

public interface ServerHandshakePacketListener extends PacketListener {
	void onHandshake(HandshakeC2SPacket packet);
}
