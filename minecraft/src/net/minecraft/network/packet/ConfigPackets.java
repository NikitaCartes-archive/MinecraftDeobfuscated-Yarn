package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.listener.ServerConfigurationPacketListener;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.c2s.config.SelectKnownPacksC2SPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.network.packet.s2c.config.ResetChatS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;
import net.minecraft.util.Identifier;

public class ConfigPackets {
	public static final PacketType<ReadyS2CPacket> FINISH_CONFIGURATION_S2C = s2c("finish_configuration");
	public static final PacketType<DynamicRegistriesS2CPacket> REGISTRY_DATA = s2c("registry_data");
	public static final PacketType<FeaturesS2CPacket> UPDATE_ENABLED_FEATURES = s2c("update_enabled_features");
	public static final PacketType<SelectKnownPacksS2CPacket> SELECT_KNOWN_PACKS_S2C = s2c("select_known_packs");
	public static final PacketType<ResetChatS2CPacket> RESET_CHAT = s2c("reset_chat");
	public static final PacketType<ReadyC2SPacket> FINISH_CONFIGURATION_C2S = c2s("finish_configuration");
	public static final PacketType<SelectKnownPacksC2SPacket> SELECT_KNOWN_PACKS_C2S = c2s("select_known_packs");

	private static <T extends Packet<ClientConfigurationPacketListener>> PacketType<T> s2c(String id) {
		return new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.ofVanilla(id));
	}

	private static <T extends Packet<ServerConfigurationPacketListener>> PacketType<T> c2s(String id) {
		return new PacketType<>(NetworkSide.SERVERBOUND, Identifier.ofVanilla(id));
	}
}
