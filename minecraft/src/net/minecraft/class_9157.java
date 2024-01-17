package net.minecraft;

import net.minecraft.network.NetworkState;
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
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket;
import net.minecraft.network.packet.s2c.common.StoreCookieS2CPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;

public class class_9157 {
	public static final class_9127<ServerConfigurationPacketListener> field_48698 = class_9147.method_56451(
		NetworkState.CONFIGURATION,
		arg -> arg.method_56454(CommonPackets.CLIENT_INFORMATION, ClientOptionsC2SPacket.CODEC)
				.method_56454(CookiePackets.COOKIE_RESPONSE, CookieResponseC2SPacket.CODEC)
				.method_56454(CommonPackets.CUSTOM_PAYLOAD_C2S, CustomPayloadC2SPacket.CODEC)
				.method_56454(ConfigPackets.FINISH_CONFIGURATION_C2S, ReadyC2SPacket.CODEC)
				.method_56454(CommonPackets.KEEP_ALIVE_C2S, KeepAliveC2SPacket.CODEC)
				.method_56454(CommonPackets.PONG, CommonPongC2SPacket.CODEC)
				.method_56454(CommonPackets.RESOURCE_PACK, ResourcePackStatusC2SPacket.CODEC)
	);
	public static final class_9127<ClientConfigurationPacketListener> field_48699 = class_9147.method_56455(
		NetworkState.CONFIGURATION,
		arg -> arg.method_56454(CookiePackets.COOKIE_REQUEST, CookieRequestS2CPacket.CODEC)
				.method_56454(CommonPackets.CUSTOM_PAYLOAD_S2C, CustomPayloadS2CPacket.field_48621)
				.method_56454(CommonPackets.DISCONNECT, DisconnectS2CPacket.CODEC)
				.method_56454(ConfigPackets.FINISH_CONFIGURATION_S2C, ReadyS2CPacket.CODEC)
				.method_56454(CommonPackets.KEEP_ALIVE_S2C, KeepAliveS2CPacket.CODEC)
				.method_56454(CommonPackets.PING, CommonPingS2CPacket.CODEC)
				.method_56454(ConfigPackets.REGISTRY_DATA, DynamicRegistriesS2CPacket.CODEC)
				.method_56454(CommonPackets.RESOURCE_PACK_POP, ResourcePackRemoveS2CPacket.CODEC)
				.method_56454(CommonPackets.RESOURCE_PACK_PUSH, ResourcePackSendS2CPacket.CODEC)
				.method_56454(CommonPackets.STORE_COOKIE, StoreCookieS2CPacket.CODEC)
				.method_56454(CommonPackets.TRANSFER, ServerTransferS2CPacket.CODEC)
				.method_56454(ConfigPackets.UPDATE_ENABLED_FEATURES, FeaturesS2CPacket.CODEC)
				.method_56454(CommonPackets.UPDATE_TAGS, SynchronizeTagsS2CPacket.CODEC)
	);
}
