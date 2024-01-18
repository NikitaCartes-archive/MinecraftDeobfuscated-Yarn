package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.world.event.PositionSource;

public record DebugGameEventListenersCustomPayload(PositionSource listenerPos, int listenerRange) implements CustomPayload {
	public static final PacketCodec<RegistryByteBuf, DebugGameEventListenersCustomPayload> PACKET_CODEC = PacketCodec.tuple(
		PositionSource.PACKET_CODEC,
		DebugGameEventListenersCustomPayload::listenerPos,
		PacketCodecs.VAR_INT,
		DebugGameEventListenersCustomPayload::listenerRange,
		DebugGameEventListenersCustomPayload::new
	);
	public static final CustomPayload.Id<DebugGameEventListenersCustomPayload> ID = CustomPayload.id("debug/game_event_listeners");

	@Override
	public CustomPayload.Id<DebugGameEventListenersCustomPayload> getId() {
		return ID;
	}
}
