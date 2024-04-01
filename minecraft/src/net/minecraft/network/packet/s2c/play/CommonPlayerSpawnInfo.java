package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public record CommonPlayerSpawnInfo(
	RegistryEntry<DimensionType> dimensionType,
	RegistryKey<World> dimension,
	long seed,
	GameMode gameMode,
	@Nullable GameMode prevGameMode,
	boolean isDebug,
	boolean isFlat,
	Optional<GlobalPos> lastDeathLocation,
	int portalCooldown,
	@Nullable UUID waitForGrid
) {
	private static final PacketCodec<RegistryByteBuf, RegistryEntry<DimensionType>> DIMENSION_TYPE_PACKET_CODEC = PacketCodecs.registryEntry(
		RegistryKeys.DIMENSION_TYPE
	);

	public CommonPlayerSpawnInfo(RegistryByteBuf buf) {
		this(
			DIMENSION_TYPE_PACKET_CODEC.decode(buf),
			buf.readRegistryKey(RegistryKeys.WORLD),
			buf.readLong(),
			GameMode.byId(buf.readByte()),
			GameMode.getOrNull(buf.readByte()),
			buf.readBoolean(),
			buf.readBoolean(),
			buf.readOptional(PacketByteBuf::readGlobalPos),
			buf.readVarInt(),
			buf.readNullable(Uuids.PACKET_CODEC)
		);
	}

	public void write(RegistryByteBuf buf) {
		DIMENSION_TYPE_PACKET_CODEC.encode(buf, this.dimensionType);
		buf.writeRegistryKey(this.dimension);
		buf.writeLong(this.seed);
		buf.writeByte(this.gameMode.getId());
		buf.writeByte(GameMode.getId(this.prevGameMode));
		buf.writeBoolean(this.isDebug);
		buf.writeBoolean(this.isFlat);
		buf.writeOptional(this.lastDeathLocation, PacketByteBuf::writeGlobalPos);
		buf.writeVarInt(this.portalCooldown);
		buf.writeNullable(this.waitForGrid, Uuids.PACKET_CODEC);
	}
}
