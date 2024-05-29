package net.minecraft.network.state;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkState;
import net.minecraft.network.NetworkStateBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.listener.ServerConfigurationPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.CookiePackets;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.c2s.config.SelectKnownPacksC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomReportDetailsS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerLinksS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket;
import net.minecraft.network.packet.s2c.common.StoreCookieS2CPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.network.packet.s2c.config.ResetChatS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;

public class ConfigurationStates {
	public static final NetworkState.Factory<ServerConfigurationPacketListener, PacketByteBuf> C2S_FACTORY = NetworkStateBuilder.c2s(
		NetworkPhase.CONFIGURATION,
		builder -> builder.add(CommonPackets.CLIENT_INFORMATION, ClientOptionsC2SPacket.CODEC)
				.add(CookiePackets.COOKIE_RESPONSE, CookieResponseC2SPacket.CODEC)
				.add(CommonPackets.CUSTOM_PAYLOAD_C2S, CustomPayloadC2SPacket.CODEC)
				.add(ConfigPackets.FINISH_CONFIGURATION_C2S, ReadyC2SPacket.CODEC)
				.add(CommonPackets.KEEP_ALIVE_C2S, KeepAliveC2SPacket.CODEC)
				.add(CommonPackets.PONG, CommonPongC2SPacket.CODEC)
				.add(CommonPackets.RESOURCE_PACK, ResourcePackStatusC2SPacket.CODEC)
				.add(ConfigPackets.SELECT_KNOWN_PACKS_C2S, SelectKnownPacksC2SPacket.CODEC)
	);
	public static final NetworkState<ServerConfigurationPacketListener> C2S = C2S_FACTORY.bind(PacketByteBuf::new);
	public static final NetworkState.Factory<ClientConfigurationPacketListener, PacketByteBuf> S2C_FACTORY = NetworkStateBuilder.s2c(
		NetworkPhase.CONFIGURATION,
		builder -> builder.add(CookiePackets.COOKIE_REQUEST, CookieRequestS2CPacket.CODEC)
				.add(CommonPackets.CUSTOM_PAYLOAD_S2C, CustomPayloadS2CPacket.CONFIGURATION_CODEC)
				.add(CommonPackets.DISCONNECT, DisconnectS2CPacket.CODEC)
				.add(ConfigPackets.FINISH_CONFIGURATION_S2C, ReadyS2CPacket.CODEC)
				.add(CommonPackets.KEEP_ALIVE_S2C, KeepAliveS2CPacket.CODEC)
				.add(CommonPackets.PING, CommonPingS2CPacket.CODEC)
				.add(ConfigPackets.RESET_CHAT, ResetChatS2CPacket.CODEC)
				.add(ConfigPackets.REGISTRY_DATA, DynamicRegistriesS2CPacket.CODEC)
				.add(CommonPackets.RESOURCE_PACK_POP, ResourcePackRemoveS2CPacket.CODEC)
				.add(CommonPackets.RESOURCE_PACK_PUSH, ResourcePackSendS2CPacket.CODEC)
				.add(CommonPackets.STORE_COOKIE, StoreCookieS2CPacket.CODEC)
				.add(CommonPackets.TRANSFER, ServerTransferS2CPacket.CODEC)
				.add(ConfigPackets.UPDATE_ENABLED_FEATURES, FeaturesS2CPacket.CODEC)
				.add(CommonPackets.UPDATE_TAGS, SynchronizeTagsS2CPacket.CODEC)
				.add(ConfigPackets.SELECT_KNOWN_PACKS_S2C, SelectKnownPacksS2CPacket.CODEC)
				.add(CommonPackets.CUSTOM_REPORT_DETAILS, CustomReportDetailsS2CPacket.CODEC)
				.add(CommonPackets.SERVER_LINKS, ServerLinksS2CPacket.CODEC)
	);
	public static final NetworkState<ClientConfigurationPacketListener> S2C = S2C_FACTORY.bind(PacketByteBuf::new);
}
