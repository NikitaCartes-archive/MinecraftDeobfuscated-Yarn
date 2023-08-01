package net.minecraft.network.listener;

import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;

public interface ServerConfigurationPacketListener extends ServerCommonPacketListener {
	@Override
	default NetworkState getState() {
		return NetworkState.CONFIGURATION;
	}

	void onReady(ReadyC2SPacket packet);
}
