package net.minecraft.network.packet.c2s.play;

import java.util.Optional;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.entry.RegistryEntry;

public record UpdateBeaconC2SPacket(Optional<RegistryEntry<StatusEffect>> primary, Optional<RegistryEntry<StatusEffect>> secondary)
	implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, UpdateBeaconC2SPacket> CODEC = PacketCodec.tuple(
		StatusEffect.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional),
		UpdateBeaconC2SPacket::primary,
		StatusEffect.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional),
		UpdateBeaconC2SPacket::secondary,
		UpdateBeaconC2SPacket::new
	);

	@Override
	public PacketType<UpdateBeaconC2SPacket> getPacketId() {
		return PlayPackets.SET_BEACON;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateBeacon(this);
	}
}
