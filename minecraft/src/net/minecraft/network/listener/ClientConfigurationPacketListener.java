package net.minecraft.network.listener;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.network.packet.s2c.config.ResetChatS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;

public interface ClientConfigurationPacketListener extends ClientCommonPacketListener {
	@Override
	default NetworkPhase getPhase() {
		return NetworkPhase.CONFIGURATION;
	}

	void onReady(ReadyS2CPacket packet);

	void onDynamicRegistries(DynamicRegistriesS2CPacket packet);

	void onFeatures(FeaturesS2CPacket packet);

	void onSelectKnownPacks(SelectKnownPacksS2CPacket packet);

	void onResetChat(ResetChatS2CPacket packet);
}
