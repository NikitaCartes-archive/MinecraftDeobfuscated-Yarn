package net.minecraft.network.packet.s2c.common;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugBeeCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugBrainCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugBreezeCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameEventCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameEventListenersCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestAddMarkerCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestClearCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGoalSelectorCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugHiveCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugNeighborsUpdateCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPathCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPoiAddedCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPoiRemovedCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPoiTicketCountCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugRaidsCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugStructuresCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugVillageSectionsCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugWorldgenAttemptCustomPayload;
import net.minecraft.util.Util;

public record CustomPayloadS2CPacket(CustomPayload payload) implements Packet<ClientCommonPacketListener> {
	private static final int MAX_PAYLOAD_SIZE = 1048576;
	public static final PacketCodec<RegistryByteBuf, CustomPayloadS2CPacket> PLAY_CODEC = CustomPayload.<RegistryByteBuf>createCodec(
			id -> UnknownCustomPayload.createCodec(id, 1048576),
			Util.make(
				Lists.<CustomPayload.Type<? super RegistryByteBuf, ?>>newArrayList(
					new CustomPayload.Type<>(BrandCustomPayload.ID, BrandCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugBeeCustomPayload.ID, DebugBeeCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugBrainCustomPayload.ID, DebugBrainCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugBreezeCustomPayload.ID, DebugBreezeCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugGameEventCustomPayload.ID, DebugGameEventCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugGameEventListenersCustomPayload.ID, DebugGameEventListenersCustomPayload.PACKET_CODEC),
					new CustomPayload.Type<>(DebugGameTestAddMarkerCustomPayload.ID, DebugGameTestAddMarkerCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugGameTestClearCustomPayload.ID, DebugGameTestClearCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugGoalSelectorCustomPayload.ID, DebugGoalSelectorCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugHiveCustomPayload.ID, DebugHiveCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugNeighborsUpdateCustomPayload.ID, DebugNeighborsUpdateCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugPathCustomPayload.ID, DebugPathCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugPoiAddedCustomPayload.ID, DebugPoiAddedCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugPoiRemovedCustomPayload.ID, DebugPoiRemovedCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugPoiTicketCountCustomPayload.ID, DebugPoiTicketCountCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugRaidsCustomPayload.ID, DebugRaidsCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugStructuresCustomPayload.ID, DebugStructuresCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugVillageSectionsCustomPayload.ID, DebugVillageSectionsCustomPayload.CODEC),
					new CustomPayload.Type<>(DebugWorldgenAttemptCustomPayload.ID, DebugWorldgenAttemptCustomPayload.CODEC)
				),
				types -> {
				}
			)
		)
		.xmap(CustomPayloadS2CPacket::new, CustomPayloadS2CPacket::payload);
	public static final PacketCodec<PacketByteBuf, CustomPayloadS2CPacket> CONFIGURATION_CODEC = CustomPayload.<PacketByteBuf>createCodec(
			id -> UnknownCustomPayload.createCodec(id, 1048576), List.of(new CustomPayload.Type<>(BrandCustomPayload.ID, BrandCustomPayload.CODEC))
		)
		.xmap(CustomPayloadS2CPacket::new, CustomPayloadS2CPacket::payload);

	@Override
	public PacketType<CustomPayloadS2CPacket> getPacketId() {
		return CommonPackets.CUSTOM_PAYLOAD_S2C;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onCustomPayload(this);
	}
}
