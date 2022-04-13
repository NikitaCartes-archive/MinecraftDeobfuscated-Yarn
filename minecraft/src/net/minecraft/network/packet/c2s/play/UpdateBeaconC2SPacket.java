package net.minecraft.network.packet.c2s.play;

import java.util.Optional;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.registry.Registry;

public class UpdateBeaconC2SPacket implements Packet<ServerPlayPacketListener> {
	private final Optional<StatusEffect> primaryEffectId;
	private final Optional<StatusEffect> secondaryEffectId;

	public UpdateBeaconC2SPacket(Optional<StatusEffect> optional, Optional<StatusEffect> optional2) {
		this.primaryEffectId = optional;
		this.secondaryEffectId = optional2;
	}

	public UpdateBeaconC2SPacket(PacketByteBuf buf) {
		this.primaryEffectId = buf.readOptional(packetByteBuf -> packetByteBuf.readRegistryValue(Registry.STATUS_EFFECT));
		this.secondaryEffectId = buf.readOptional(packetByteBuf -> packetByteBuf.readRegistryValue(Registry.STATUS_EFFECT));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeOptional(this.primaryEffectId, (packetByteBuf, statusEffect) -> packetByteBuf.writeRegistryValue(Registry.STATUS_EFFECT, statusEffect));
		buf.writeOptional(this.secondaryEffectId, (packetByteBuf, statusEffect) -> packetByteBuf.writeRegistryValue(Registry.STATUS_EFFECT, statusEffect));
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateBeacon(this);
	}

	public Optional<StatusEffect> getPrimaryEffectId() {
		return this.primaryEffectId;
	}

	public Optional<StatusEffect> getSecondaryEffectId() {
		return this.secondaryEffectId;
	}
}
