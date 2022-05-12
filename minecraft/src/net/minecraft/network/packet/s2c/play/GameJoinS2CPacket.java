package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
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
	boolean flatWorld
) implements Packet<ClientPlayPacketListener> {
	public GameJoinS2CPacket(PacketByteBuf buf) {
		this(
			buf.readInt(),
			buf.readBoolean(),
			GameMode.byId(buf.readByte()),
			GameMode.getOrNull(buf.readByte()),
			buf.readCollection(Sets::newHashSetWithExpectedSize, b -> b.readRegistryKey(Registry.WORLD_KEY)),
			buf.decode(DynamicRegistryManager.CODEC).toImmutable(),
			buf.readRegistryKey(Registry.DIMENSION_TYPE_KEY),
			buf.readRegistryKey(Registry.WORLD_KEY),
			buf.readLong(),
			buf.readVarInt(),
			buf.readVarInt(),
			buf.readVarInt(),
			buf.readBoolean(),
			buf.readBoolean(),
			buf.readBoolean(),
			buf.readBoolean()
		);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.playerEntityId);
		buf.writeBoolean(this.hardcore);
		buf.writeByte(this.gameMode.getId());
		buf.writeByte(GameMode.getId(this.previousGameMode));
		buf.writeCollection(this.dimensionIds, PacketByteBuf::writeRegistryKey);
		buf.encode(DynamicRegistryManager.CODEC, this.registryManager);
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
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameJoin(this);
	}
}
