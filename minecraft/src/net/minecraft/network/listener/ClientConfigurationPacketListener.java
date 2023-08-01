package net.minecraft.network.listener;

import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;

public interface ClientConfigurationPacketListener extends ClientCommonPacketListener {
	@Override
	default NetworkState getState() {
		return NetworkState.CONFIGURATION;
	}

	void onReady(ReadyS2CPacket packet);

	void onDynamicRegistries(DynamicRegistriesS2CPacket packet);

	void onFeatures(FeaturesS2CPacket packet);
}
