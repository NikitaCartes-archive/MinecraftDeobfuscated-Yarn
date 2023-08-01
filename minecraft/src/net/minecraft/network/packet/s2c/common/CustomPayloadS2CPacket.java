package net.minecraft.network.packet.s2c.common;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.network.packet.s2c.custom.BrandCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugBeeCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugBrainCustomPayload;
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
import net.minecraft.util.Identifier;

public record CustomPayloadS2CPacket(CustomPayload payload) implements Packet<ClientCommonPacketListener> {
	private static final int MAX_PAYLOAD_SIZE = 1048576;
	private static final Map<Identifier, PacketByteBuf.PacketReader<? extends CustomPayload>> ID_TO_READER = ImmutableMap.<Identifier, PacketByteBuf.PacketReader<? extends CustomPayload>>builder()
		.put(BrandCustomPayload.ID, BrandCustomPayload::new)
		.put(DebugBeeCustomPayload.ID, DebugBeeCustomPayload::new)
		.put(DebugBrainCustomPayload.ID, DebugBrainCustomPayload::new)
		.put(DebugGameEventCustomPayload.ID, DebugGameEventCustomPayload::new)
		.put(DebugGameEventListenersCustomPayload.ID, DebugGameEventListenersCustomPayload::new)
		.put(DebugGameTestAddMarkerCustomPayload.ID, DebugGameTestAddMarkerCustomPayload::new)
		.put(DebugGameTestClearCustomPayload.ID, DebugGameTestClearCustomPayload::new)
		.put(DebugGoalSelectorCustomPayload.ID, DebugGoalSelectorCustomPayload::new)
		.put(DebugHiveCustomPayload.ID, DebugHiveCustomPayload::new)
		.put(DebugNeighborsUpdateCustomPayload.ID, DebugNeighborsUpdateCustomPayload::new)
		.put(DebugPathCustomPayload.ID, DebugPathCustomPayload::new)
		.put(DebugPoiAddedCustomPayload.ID, DebugPoiAddedCustomPayload::new)
		.put(DebugPoiRemovedCustomPayload.ID, DebugPoiRemovedCustomPayload::new)
		.put(DebugPoiTicketCountCustomPayload.ID, DebugPoiTicketCountCustomPayload::new)
		.put(DebugRaidsCustomPayload.ID, DebugRaidsCustomPayload::new)
		.put(DebugStructuresCustomPayload.ID, DebugStructuresCustomPayload::new)
		.put(DebugVillageSectionsCustomPayload.ID, DebugVillageSectionsCustomPayload::new)
		.put(DebugWorldgenAttemptCustomPayload.ID, DebugWorldgenAttemptCustomPayload::new)
		.build();

	public CustomPayloadS2CPacket(PacketByteBuf buf) {
		this(readPayload(buf.readIdentifier(), buf));
	}

	private static CustomPayload readPayload(Identifier id, PacketByteBuf buf) {
		PacketByteBuf.PacketReader<? extends CustomPayload> packetReader = (PacketByteBuf.PacketReader<? extends CustomPayload>)ID_TO_READER.get(id);
		return (CustomPayload)(packetReader != null ? (CustomPayload)packetReader.apply(buf) : readUnknownPayload(id, buf));
	}

	private static UnknownCustomPayload readUnknownPayload(Identifier id, PacketByteBuf buf) {
		int i = buf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			buf.skipBytes(i);
			return new UnknownCustomPayload(id);
		} else {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeIdentifier(this.payload.id());
		this.payload.write(buf);
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onCustomPayload(this);
	}
}
