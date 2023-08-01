package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

public record DebugGameEventListenersCustomPayload(PositionSource listenerPos, int listenerRange) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/game_event_listeners");

	public DebugGameEventListenersCustomPayload(PacketByteBuf buf) {
		this(PositionSourceType.read(buf), buf.readVarInt());
	}

	@Override
	public void write(PacketByteBuf buf) {
		PositionSourceType.write(this.listenerPos, buf);
		buf.writeVarInt(this.listenerRange);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
