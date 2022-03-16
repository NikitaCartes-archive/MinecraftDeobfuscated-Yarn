package net.minecraft.network.packet.c2s.play;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.registry.Registry;

public class UpdateBeaconC2SPacket implements Packet<ServerPlayPacketListener> {
	private final StatusEffect primaryEffectId;
	private final StatusEffect secondaryEffectId;

	public UpdateBeaconC2SPacket(StatusEffect statusEffect, StatusEffect statusEffect2) {
		this.primaryEffectId = statusEffect;
		this.secondaryEffectId = statusEffect2;
	}

	public UpdateBeaconC2SPacket(PacketByteBuf buf) {
		this.primaryEffectId = buf.readRegistryValue(Registry.STATUS_EFFECT);
		this.secondaryEffectId = buf.readRegistryValue(Registry.STATUS_EFFECT);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeRegistryValue(Registry.STATUS_EFFECT, this.primaryEffectId);
		buf.writeRegistryValue(Registry.STATUS_EFFECT, this.secondaryEffectId);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateBeacon(this);
	}

	public StatusEffect getPrimaryEffectId() {
		return this.primaryEffectId;
	}

	public StatusEffect getSecondaryEffectId() {
		return this.secondaryEffectId;
	}
}
