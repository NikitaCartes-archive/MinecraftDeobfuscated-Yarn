package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class GameJoinS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int playerEntityId;
	private final long sha256Seed;
	private final boolean hardcore;
	private final GameMode gameMode;
	@Nullable
	private final GameMode previousGameMode;
	private final Set<RegistryKey<World>> dimensionIds;
	private final DynamicRegistryManager.Impl registryManager;
	private final DimensionType dimensionType;
	private final RegistryKey<World> dimensionId;
	private final int maxPlayers;
	private final int viewDistance;
	private final boolean reducedDebugInfo;
	private final boolean showDeathScreen;
	private final boolean debugWorld;
	private final boolean flatWorld;

	public GameJoinS2CPacket(
		int playerEntityId,
		GameMode gameMode,
		@Nullable GameMode previousGameMode,
		long sha256Seed,
		boolean hardcore,
		Set<RegistryKey<World>> dimensionIds,
		DynamicRegistryManager.Impl registryManager,
		DimensionType dimensionType,
		RegistryKey<World> dimensionId,
		int maxPlayers,
		int chunkLoadDistance,
		boolean reducedDebugInfo,
		boolean showDeathScreen,
		boolean debugWorld,
		boolean flatWorld
	) {
		this.playerEntityId = playerEntityId;
		this.dimensionIds = dimensionIds;
		this.registryManager = registryManager;
		this.dimensionType = dimensionType;
		this.dimensionId = dimensionId;
		this.sha256Seed = sha256Seed;
		this.gameMode = gameMode;
		this.previousGameMode = previousGameMode;
		this.maxPlayers = maxPlayers;
		this.hardcore = hardcore;
		this.viewDistance = chunkLoadDistance;
		this.reducedDebugInfo = reducedDebugInfo;
		this.showDeathScreen = showDeathScreen;
		this.debugWorld = debugWorld;
		this.flatWorld = flatWorld;
	}

	public GameJoinS2CPacket(PacketByteBuf buf) {
		this.playerEntityId = buf.readInt();
		this.hardcore = buf.readBoolean();
		this.gameMode = GameMode.byId(buf.readByte());
		this.previousGameMode = GameMode.getOrNull(buf.readByte());
		this.dimensionIds = buf.readCollection(Sets::newHashSetWithExpectedSize, b -> RegistryKey.of(Registry.DIMENSION, b.readIdentifier()));
		this.registryManager = buf.decode(DynamicRegistryManager.Impl.CODEC);
		this.dimensionType = (DimensionType)buf.decode(DimensionType.REGISTRY_CODEC).get();
		this.dimensionId = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
		this.sha256Seed = buf.readLong();
		this.maxPlayers = buf.readVarInt();
		this.viewDistance = buf.readVarInt();
		this.reducedDebugInfo = buf.readBoolean();
		this.showDeathScreen = buf.readBoolean();
		this.debugWorld = buf.readBoolean();
		this.flatWorld = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.playerEntityId);
		buf.writeBoolean(this.hardcore);
		buf.writeByte(this.gameMode.getId());
		buf.writeByte(GameMode.getId(this.previousGameMode));
		buf.writeCollection(this.dimensionIds, (b, dimension) -> b.writeIdentifier(dimension.getValue()));
		buf.encode(DynamicRegistryManager.Impl.CODEC, this.registryManager);
		buf.encode(DimensionType.REGISTRY_CODEC, () -> this.dimensionType);
		buf.writeIdentifier(this.dimensionId.getValue());
		buf.writeLong(this.sha256Seed);
		buf.writeVarInt(this.maxPlayers);
		buf.writeVarInt(this.viewDistance);
		buf.writeBoolean(this.reducedDebugInfo);
		buf.writeBoolean(this.showDeathScreen);
		buf.writeBoolean(this.debugWorld);
		buf.writeBoolean(this.flatWorld);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameJoin(this);
	}

	@Environment(EnvType.CLIENT)
	public int getEntityId() {
		return this.playerEntityId;
	}

	@Environment(EnvType.CLIENT)
	public long getSha256Seed() {
		return this.sha256Seed;
	}

	@Environment(EnvType.CLIENT)
	public boolean isHardcore() {
		return this.hardcore;
	}

	@Environment(EnvType.CLIENT)
	public GameMode getGameMode() {
		return this.gameMode;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public GameMode getPreviousGameMode() {
		return this.previousGameMode;
	}

	@Environment(EnvType.CLIENT)
	public Set<RegistryKey<World>> getDimensionIds() {
		return this.dimensionIds;
	}

	@Environment(EnvType.CLIENT)
	public DynamicRegistryManager getRegistryManager() {
		return this.registryManager;
	}

	@Environment(EnvType.CLIENT)
	public DimensionType getDimensionType() {
		return this.dimensionType;
	}

	@Environment(EnvType.CLIENT)
	public RegistryKey<World> getDimensionId() {
		return this.dimensionId;
	}

	@Environment(EnvType.CLIENT)
	public int getViewDistance() {
		return this.viewDistance;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasReducedDebugInfo() {
		return this.reducedDebugInfo;
	}

	@Environment(EnvType.CLIENT)
	public boolean showsDeathScreen() {
		return this.showDeathScreen;
	}

	@Environment(EnvType.CLIENT)
	public boolean isDebugWorld() {
		return this.debugWorld;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFlatWorld() {
		return this.flatWorld;
	}
}
