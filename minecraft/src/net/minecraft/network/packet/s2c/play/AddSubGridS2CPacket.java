package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.entity.GridCarrierEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Uuids;
import net.minecraft.world.Grid;
import net.minecraft.world.biome.Biome;

public record AddSubGridS2CPacket(int id, UUID uuid, double x, double y, double z, Grid blocks, RegistryEntry<Biome> biome)
	implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, AddSubGridS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		AddSubGridS2CPacket::id,
		Uuids.PACKET_CODEC,
		AddSubGridS2CPacket::uuid,
		PacketCodecs.DOUBLE,
		AddSubGridS2CPacket::x,
		PacketCodecs.DOUBLE,
		AddSubGridS2CPacket::y,
		PacketCodecs.DOUBLE,
		AddSubGridS2CPacket::z,
		Grid.PACKET_CODEC,
		AddSubGridS2CPacket::blocks,
		PacketCodecs.registryEntry(RegistryKeys.BIOME),
		AddSubGridS2CPacket::biome,
		AddSubGridS2CPacket::new
	);

	public AddSubGridS2CPacket(GridCarrierEntity gridCarrierEntity) {
		this(
			gridCarrierEntity.getId(),
			gridCarrierEntity.getUuid(),
			gridCarrierEntity.getX(),
			gridCarrierEntity.getY(),
			gridCarrierEntity.getZ(),
			gridCarrierEntity.method_58953().getGrid().copy(),
			gridCarrierEntity.method_58953().getBiome()
		);
	}

	@Override
	public PacketType<AddSubGridS2CPacket> getPacketId() {
		return PlayPackets.ADD_SUB_GRID;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onAddSubGrid(this);
	}
}
