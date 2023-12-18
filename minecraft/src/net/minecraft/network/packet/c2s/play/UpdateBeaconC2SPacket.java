package net.minecraft.network.packet.c2s.play;

import java.util.Optional;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public record UpdateBeaconC2SPacket(Optional<RegistryEntry<StatusEffect>> primary, Optional<RegistryEntry<StatusEffect>> secondary)
	implements Packet<ServerPlayPacketListener> {
	public UpdateBeaconC2SPacket(PacketByteBuf buf) {
		this(
			buf.readOptional(buf2 -> buf2.readRegistryValue(Registries.STATUS_EFFECT.getIndexedEntries())),
			buf.readOptional(buf2 -> buf2.readRegistryValue(Registries.STATUS_EFFECT.getIndexedEntries()))
		);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeOptional(this.primary, (buf2, primary) -> buf2.writeRegistryValue(Registries.STATUS_EFFECT.getIndexedEntries(), primary));
		buf.writeOptional(this.secondary, (buf2, secondary) -> buf2.writeRegistryValue(Registries.STATUS_EFFECT.getIndexedEntries(), secondary));
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateBeacon(this);
	}
}
