package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;

public record DebugGameEventCustomPayload(RegistryKey<GameEvent> type, Vec3d pos) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/game_event");

	public DebugGameEventCustomPayload(PacketByteBuf buf) {
		this(buf.readRegistryKey(RegistryKeys.GAME_EVENT), buf.readVec3d());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeRegistryKey(this.type);
		buf.writeVec3d(this.pos);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
