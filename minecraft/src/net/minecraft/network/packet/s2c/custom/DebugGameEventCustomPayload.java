package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;

public record DebugGameEventCustomPayload(RegistryKey<GameEvent> gameEventType, Vec3d pos) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugGameEventCustomPayload> CODEC = CustomPayload.codecOf(
		DebugGameEventCustomPayload::write, DebugGameEventCustomPayload::new
	);
	public static final CustomPayload.Id<DebugGameEventCustomPayload> ID = CustomPayload.id("debug/game_event");

	private DebugGameEventCustomPayload(PacketByteBuf buf) {
		this(buf.readRegistryKey(RegistryKeys.GAME_EVENT), buf.readVec3d());
	}

	private void write(PacketByteBuf buf) {
		buf.writeRegistryKey(this.gameEventType);
		buf.writeVec3d(this.pos);
	}

	@Override
	public CustomPayload.Id<DebugGameEventCustomPayload> getId() {
		return ID;
	}
}
