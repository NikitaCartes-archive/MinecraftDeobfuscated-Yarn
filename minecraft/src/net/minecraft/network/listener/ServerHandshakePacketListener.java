package net.minecraft.network.listener;

import net.minecraft.server.network.packet.HandshakeServerPacket;

public interface ServerHandshakePacketListener extends PacketListener {
	void onHandshake(HandshakeServerPacket handshakeServerPacket);
}
