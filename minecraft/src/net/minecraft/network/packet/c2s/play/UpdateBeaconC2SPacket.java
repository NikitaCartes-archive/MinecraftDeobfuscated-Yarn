package net.minecraft.network.packet.c2s.play;

import java.util.Optional;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;

public class UpdateBeaconC2SPacket implements Packet<ServerPlayPacketListener> {
	private final Optional<StatusEffect> primaryEffectId;
	private final Optional<StatusEffect> secondaryEffectId;

	public UpdateBeaconC2SPacket(Optional<StatusEffect> primaryEffectId, Optional<StatusEffect> secondaryEffectId) {
		this.primaryEffectId = primaryEffectId;
		this.secondaryEffectId = secondaryEffectId;
	}

	public UpdateBeaconC2SPacket(PacketByteBuf buf) {
		this.primaryEffectId = buf.readOptional(buf2 -> buf2.readRegistryValue(Registries.STATUS_EFFECT));
		this.secondaryEffectId = buf.readOptional(buf2 -> buf2.readRegistryValue(Registries.STATUS_EFFECT));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeOptional(this.primaryEffectId, (buf2, primaryEffectId) -> buf2.writeRegistryValue(Registries.STATUS_EFFECT, primaryEffectId));
		buf.writeOptional(this.secondaryEffectId, (buf2, secondaryEffectId) -> buf2.writeRegistryValue(Registries.STATUS_EFFECT, secondaryEffectId));
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
