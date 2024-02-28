package net.minecraft.network.packet.c2s.config;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.VersionedIdentifier;

public record SelectKnownPacksC2SPacket(List<VersionedIdentifier> knownPacks) implements Packet<ServerConfigurationPacketListener> {
	public static final PacketCodec<ByteBuf, SelectKnownPacksC2SPacket> CODEC = PacketCodec.tuple(
		VersionedIdentifier.PACKET_CODEC.collect(PacketCodecs.toList(64)), SelectKnownPacksC2SPacket::knownPacks, SelectKnownPacksC2SPacket::new
	);

	@Override
	public PacketType<SelectKnownPacksC2SPacket> getPacketId() {
		return ConfigPackets.SELECT_KNOWN_PACKS_C2S;
	}

	public void apply(ServerConfigurationPacketListener serverConfigurationPacketListener) {
		serverConfigurationPacketListener.onSelectKnownPacks(this);
	}
}
