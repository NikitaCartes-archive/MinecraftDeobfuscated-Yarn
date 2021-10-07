package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.lang.runtime.ObjectMethods;
import java.util.Set;
import java.util.function.Supplier;
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

public final class GameJoinS2CPacket extends Record implements Packet {
	private final int playerEntityId;
	private final boolean hardcore;
	private final GameMode gameMode;
	@Nullable
	private final GameMode previousGameMode;
	private final Set<RegistryKey<World>> dimensionIds;
	private final DynamicRegistryManager.Impl registryManager;
	private final DimensionType dimensionType;
	private final RegistryKey<World> dimensionId;
	private final long sha256Seed;
	private final int maxPlayers;
	private final int viewDistance;
	private final int simulationDistance;
	private final boolean reducedDebugInfo;
	private final boolean showDeathScreen;
	private final boolean debugWorld;
	private final boolean flatWorld;

	public GameJoinS2CPacket(PacketByteBuf buf) {
		this(
			buf.readInt(),
			buf.readBoolean(),
			GameMode.byId(buf.readByte()),
			GameMode.getOrNull(buf.readByte()),
			buf.readCollection(Sets::newHashSetWithExpectedSize, b -> RegistryKey.of(Registry.WORLD_KEY, b.readIdentifier())),
			buf.decode(DynamicRegistryManager.Impl.CODEC),
			(DimensionType)((Supplier)buf.decode(DimensionType.REGISTRY_CODEC)).get(),
			RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier()),
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

	public GameJoinS2CPacket(
		int playerEntityId,
		boolean bl,
		GameMode previousGameMode,
		@Nullable GameMode gameMode,
		Set<RegistryKey<World>> set,
		DynamicRegistryManager.Impl impl,
		DimensionType dimensionType,
		RegistryKey<World> registryKey,
		long l,
		int maxPlayers,
		int chunkLoadDistance,
		int i,
		boolean bl2,
		boolean bl3,
		boolean bl4,
		boolean bl5
	) {
		this.playerEntityId = playerEntityId;
		this.hardcore = bl;
		this.gameMode = previousGameMode;
		this.previousGameMode = gameMode;
		this.dimensionIds = set;
		this.registryManager = impl;
		this.dimensionType = dimensionType;
		this.dimensionId = registryKey;
		this.sha256Seed = l;
		this.maxPlayers = maxPlayers;
		this.viewDistance = chunkLoadDistance;
		this.simulationDistance = i;
		this.reducedDebugInfo = bl2;
		this.showDeathScreen = bl3;
		this.debugWorld = bl4;
		this.flatWorld = bl5;
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.playerEntityId);
		buf.writeBoolean(this.hardcore);
		buf.writeByte(this.gameMode.getId());
		buf.writeByte(GameMode.getId(this.previousGameMode));
		buf.writeCollection(this.dimensionIds, (b, dimension) -> b.writeIdentifier(dimension.getValue()));
		buf.encode(DynamicRegistryManager.Impl.CODEC, this.registryManager);
		buf.encode(DimensionType.REGISTRY_CODEC, (Supplier)() -> this.dimensionType);
		buf.writeIdentifier(this.dimensionId.getValue());
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",GameJoinS2CPacket,"playerId;hardcore;gameType;previousGameType;levels;registryHolder;dimensionType;dimension;seed;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;isDebug;isFlat",GameJoinS2CPacket::playerEntityId,GameJoinS2CPacket::hardcore,GameJoinS2CPacket::gameMode,GameJoinS2CPacket::previousGameMode,GameJoinS2CPacket::dimensionIds,GameJoinS2CPacket::registryManager,GameJoinS2CPacket::dimensionType,GameJoinS2CPacket::dimensionId,GameJoinS2CPacket::sha256Seed,GameJoinS2CPacket::maxPlayers,GameJoinS2CPacket::viewDistance,GameJoinS2CPacket::simulationDistance,GameJoinS2CPacket::reducedDebugInfo,GameJoinS2CPacket::showDeathScreen,GameJoinS2CPacket::debugWorld,GameJoinS2CPacket::flatWorld>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",GameJoinS2CPacket,"playerId;hardcore;gameType;previousGameType;levels;registryHolder;dimensionType;dimension;seed;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;isDebug;isFlat",GameJoinS2CPacket::playerEntityId,GameJoinS2CPacket::hardcore,GameJoinS2CPacket::gameMode,GameJoinS2CPacket::previousGameMode,GameJoinS2CPacket::dimensionIds,GameJoinS2CPacket::registryManager,GameJoinS2CPacket::dimensionType,GameJoinS2CPacket::dimensionId,GameJoinS2CPacket::sha256Seed,GameJoinS2CPacket::maxPlayers,GameJoinS2CPacket::viewDistance,GameJoinS2CPacket::simulationDistance,GameJoinS2CPacket::reducedDebugInfo,GameJoinS2CPacket::showDeathScreen,GameJoinS2CPacket::debugWorld,GameJoinS2CPacket::flatWorld>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",GameJoinS2CPacket,"playerId;hardcore;gameType;previousGameType;levels;registryHolder;dimensionType;dimension;seed;maxPlayers;chunkRadius;simulationDistance;reducedDebugInfo;showDeathScreen;isDebug;isFlat",GameJoinS2CPacket::playerEntityId,GameJoinS2CPacket::hardcore,GameJoinS2CPacket::gameMode,GameJoinS2CPacket::previousGameMode,GameJoinS2CPacket::dimensionIds,GameJoinS2CPacket::registryManager,GameJoinS2CPacket::dimensionType,GameJoinS2CPacket::dimensionId,GameJoinS2CPacket::sha256Seed,GameJoinS2CPacket::maxPlayers,GameJoinS2CPacket::viewDistance,GameJoinS2CPacket::simulationDistance,GameJoinS2CPacket::reducedDebugInfo,GameJoinS2CPacket::showDeathScreen,GameJoinS2CPacket::debugWorld,GameJoinS2CPacket::flatWorld>(
			this, object
		);
	}

	public int playerEntityId() {
		return this.playerEntityId;
	}

	public boolean hardcore() {
		return this.hardcore;
	}

	public GameMode gameMode() {
		return this.gameMode;
	}

	@Nullable
	public GameMode previousGameMode() {
		return this.previousGameMode;
	}

	public Set<RegistryKey<World>> dimensionIds() {
		return this.dimensionIds;
	}

	public DynamicRegistryManager.Impl registryManager() {
		return this.registryManager;
	}

	public DimensionType dimensionType() {
		return this.dimensionType;
	}

	public RegistryKey<World> dimensionId() {
		return this.dimensionId;
	}

	public long sha256Seed() {
		return this.sha256Seed;
	}

	public int maxPlayers() {
		return this.maxPlayers;
	}

	public int viewDistance() {
		return this.viewDistance;
	}

	public int simulationDistance() {
		return this.simulationDistance;
	}

	public boolean reducedDebugInfo() {
		return this.reducedDebugInfo;
	}

	public boolean showDeathScreen() {
		return this.showDeathScreen;
	}

	public boolean debugWorld() {
		return this.debugWorld;
	}

	public boolean flatWorld() {
		return this.flatWorld;
	}
}
