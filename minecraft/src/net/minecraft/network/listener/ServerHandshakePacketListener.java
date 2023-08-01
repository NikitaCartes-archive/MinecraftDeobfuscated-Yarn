package net.minecraft.network.listener;

import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;

public interface ServerHandshakePacketListener extends ServerCrashSafePacketListener {
	@Override
	default NetworkState getState() {
		return NetworkState.HANDSHAKING;
	}

	void onHandshake(HandshakeC2SPacket packet);
}
