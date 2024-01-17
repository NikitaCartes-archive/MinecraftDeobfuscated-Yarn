package net.minecraft.network.packet.s2c.common;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
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

public record CustomPayloadS2CPacket(CustomPayload payload) implements Packet<ClientCommonPacketListener> {
	private static final int MAX_PAYLOAD_SIZE = 1048576;
	public static final PacketCodec<RegistryByteBuf, CustomPayloadS2CPacket> field_48620 = CustomPayload.<RegistryByteBuf>createCodec(
			identifier -> UnknownCustomPayload.createCodec(identifier, 1048576),
			List.of(
				new CustomPayload.Type<>(BrandCustomPayload.KEY, BrandCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugBeeCustomPayload.KEY, DebugBeeCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugBrainCustomPayload.KEY, DebugBrainCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugBreezeCustomPayload.KEY, DebugBreezeCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugGameEventCustomPayload.KEY, DebugGameEventCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugGameEventListenersCustomPayload.PAYLOAD_TYPE, DebugGameEventListenersCustomPayload.PACKET_CODEC),
				new CustomPayload.Type<>(DebugGameTestAddMarkerCustomPayload.KEY, DebugGameTestAddMarkerCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugGameTestClearCustomPayload.KEY, DebugGameTestClearCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugGoalSelectorCustomPayload.KEY, DebugGoalSelectorCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugHiveCustomPayload.KEY, DebugHiveCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugNeighborsUpdateCustomPayload.KEY, DebugNeighborsUpdateCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugPathCustomPayload.KEY, DebugPathCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugPoiAddedCustomPayload.KEY, DebugPoiAddedCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugPoiRemovedCustomPayload.KEY, DebugPoiRemovedCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugPoiTicketCountCustomPayload.KEY, DebugPoiTicketCountCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugRaidsCustomPayload.KEY, DebugRaidsCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugStructuresCustomPayload.KEY, DebugStructuresCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugVillageSectionsCustomPayload.KEY, DebugVillageSectionsCustomPayload.CODEC),
				new CustomPayload.Type<>(DebugWorldgenAttemptCustomPayload.KEY, DebugWorldgenAttemptCustomPayload.CODEC)
			)
		)
		.xmap(CustomPayloadS2CPacket::new, CustomPayloadS2CPacket::payload);
	public static final PacketCodec<PacketByteBuf, CustomPayloadS2CPacket> field_48621 = CustomPayload.<PacketByteBuf>createCodec(
			identifier -> UnknownCustomPayload.createCodec(identifier, 1048576), List.of(new CustomPayload.Type<>(BrandCustomPayload.KEY, BrandCustomPayload.CODEC))
		)
		.xmap(CustomPayloadS2CPacket::new, CustomPayloadS2CPacket::payload);

	@Override
	public PacketIdentifier<CustomPayloadS2CPacket> getPacketId() {
		return CommonPackets.CUSTOM_PAYLOAD_S2C;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onCustomPayload(this);
	}
}
