package net.minecraft.network.listener;

import net.minecraft.server.network.packet.HandshakeC2SPacket;

public interface ServerHandshakePacketListener extends PacketListener {
	void onHandshake(HandshakeC2SPacket handshakeC2SPacket);
}
