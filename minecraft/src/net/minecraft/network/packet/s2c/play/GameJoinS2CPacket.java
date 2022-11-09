package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public record GameJoinS2CPacket(
	int playerEntityId,
	boolean hardcore,
	GameMode gameMode,
	@Nullable GameMode previousGameMode,
	Set<RegistryKey<World>> dimensionIds,
	DynamicRegistryManager.Immutable registryManager,
	RegistryKey<DimensionType> dimensionType,
	RegistryKey<World> dimensionId,
	long sha256Seed,
	int maxPlayers,
	int viewDistance,
	int simulationDistance,
	boolean reducedDebugInfo,
	boolean showDeathScreen,
	boolean debugWorld,
	boolean flatWorld,
	Optional<GlobalPos> lastDeathLocation
) implements Packet<ClientPlayPacketListener> {
	public GameJoinS2CPacket(PacketByteBuf buf) {
		this(
			buf.readInt(),
			buf.readBoolean(),
			GameMode.byId(buf.readByte()),
			GameMode.getOrNull(buf.readByte()),
			buf.readCollection(Sets::newHashSetWithExpectedSize, b -> b.readRegistryKey(RegistryKeys.WORLD)),
			buf.decode(SerializableRegistries.CODEC).toImmutable(),
			buf.readRegistryKey(RegistryKeys.DIMENSION_TYPE),
			buf.readRegistryKey(RegistryKeys.WORLD),
			buf.readLong(),
			buf.readVarInt(),
			buf.readVarInt(),
			buf.readVarInt(),
			buf.readBoolean(),
			buf.readBoolean(),
			buf.readBoolean(),
			buf.readBoolean(),
			buf.readOptional(PacketByteBuf::readGlobalPos)
		);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.playerEntityId);
		buf.writeBoolean(this.hardcore);
		buf.writeByte(this.gameMode.getId());
		buf.writeByte(GameMode.getId(this.previousGameMode));
		buf.writeCollection(this.dimensionIds, PacketByteBuf::writeRegistryKey);
		buf.encode(SerializableRegistries.CODEC, this.registryManager);
		buf.writeRegistryKey(this.dimensionType);
		buf.writeRegistryKey(this.dimensionId);
		buf.writeLong(this.sha256Seed);
		buf.writeVarInt(this.maxPlayers);
		buf.writeVarInt(this.viewDistance);
		buf.writeVarInt(this.simulationDistance);
		buf.writeBoolean(this.reducedDebugInfo);
		buf.writeBoolean(this.showDeathScreen);
		buf.writeBoolean(this.debugWorld);
		buf.writeBoolean(this.flatWorld);
		buf.writeOptional(this.lastDeathLocation, PacketByteBuf::writeGlobalPos);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameJoin(this);
	}
}
