package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public record CommonPlayerSpawnInfo(
	RegistryKey<DimensionType> dimensionType,
	RegistryKey<World> dimension,
	long seed,
	GameMode gameMode,
	@Nullable GameMode prevGameMode,
	boolean isDebug,
	boolean isFlat,
	Optional<GlobalPos> lastDeathLocation,
	int portalCooldown
) {
	public CommonPlayerSpawnInfo(PacketByteBuf buf) {
		this(
			buf.readRegistryKey(RegistryKeys.DIMENSION_TYPE),
			buf.readRegistryKey(RegistryKeys.WORLD),
			buf.readLong(),
			GameMode.byId(buf.readByte()),
			GameMode.getOrNull(buf.readByte()),
			buf.readBoolean(),
			buf.readBoolean(),
			buf.readOptional(PacketByteBuf::readGlobalPos),
			buf.readVarInt()
		);
	}

	public void write(PacketByteBuf buf) {
		buf.writeRegistryKey(this.dimensionType);
		buf.writeRegistryKey(this.dimension);
		buf.writeLong(this.seed);
		buf.writeByte(this.gameMode.getId());
		buf.writeByte(GameMode.getId(this.prevGameMode));
		buf.writeBoolean(this.isDebug);
		buf.writeBoolean(this.isFlat);
		buf.writeOptional(this.lastDeathLocation, PacketByteBuf::writeGlobalPos);
		buf.writeVarInt(this.portalCooldown);
	}
}
