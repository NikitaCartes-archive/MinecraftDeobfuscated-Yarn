package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

public record RemoveEntityStatusEffectS2CPacket(int entityId, RegistryEntry<StatusEffect> effect) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, RemoveEntityStatusEffectS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		RemoveEntityStatusEffectS2CPacket::entityId,
		StatusEffect.ENTRY_PACKET_CODEC,
		RemoveEntityStatusEffectS2CPacket::effect,
		RemoveEntityStatusEffectS2CPacket::new
	);

	@Override
	public PacketType<RemoveEntityStatusEffectS2CPacket> getPacketId() {
		return PlayPackets.REMOVE_MOB_EFFECT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onRemoveEntityStatusEffect(this);
	}

	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityById(this.entityId);
	}
}
