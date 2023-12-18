package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

public record RemoveEntityStatusEffectS2CPacket(int entityId, RegistryEntry<StatusEffect> effect) implements Packet<ClientPlayPacketListener> {
	public RemoveEntityStatusEffectS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt(), buf.readRegistryValue(Registries.STATUS_EFFECT.getIndexedEntries()));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeRegistryValue(Registries.STATUS_EFFECT.getIndexedEntries(), this.effect);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onRemoveEntityStatusEffect(this);
	}

	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}
}
