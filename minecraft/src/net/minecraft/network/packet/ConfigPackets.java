package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.listener.ServerConfigurationPacketListener;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.util.Identifier;

public class ConfigPackets {
	public static final PacketIdentifier<ReadyS2CPacket> FINISH_CONFIGURATION_S2C = s2c("finish_configuration");
	public static final PacketIdentifier<DynamicRegistriesS2CPacket> REGISTRY_DATA = s2c("registry_data");
	public static final PacketIdentifier<FeaturesS2CPacket> UPDATE_ENABLED_FEATURES = s2c("update_enabled_features");
	public static final PacketIdentifier<ReadyC2SPacket> FINISH_CONFIGURATION_C2S = c2s("finish_configuration");

	private static <T extends Packet<ClientConfigurationPacketListener>> PacketIdentifier<T> s2c(String id) {
		return new PacketIdentifier<>(NetworkSide.CLIENTBOUND, new Identifier(id));
	}

	private static <T extends Packet<ServerConfigurationPacketListener>> PacketIdentifier<T> c2s(String id) {
		return new PacketIdentifier<>(NetworkSide.SERVERBOUND, new Identifier(id));
	}
}
