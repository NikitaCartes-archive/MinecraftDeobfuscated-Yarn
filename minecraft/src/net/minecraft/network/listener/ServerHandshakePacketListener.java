package net.minecraft.network.listener;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;

public interface ServerHandshakePacketListener extends ServerCrashSafePacketListener {
	@Override
	default NetworkPhase getPhase() {
		return NetworkPhase.HANDSHAKING;
	}

	void onHandshake(HandshakeC2SPacket packet);
}
