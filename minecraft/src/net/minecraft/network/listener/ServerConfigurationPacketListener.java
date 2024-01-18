package net.minecraft.network.listener;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;

public interface ServerConfigurationPacketListener extends ServerCommonPacketListener {
	@Override
	default NetworkPhase getPhase() {
		return NetworkPhase.CONFIGURATION;
	}

	void onReady(ReadyC2SPacket packet);
}
