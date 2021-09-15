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

public record GameJoinS2CPacket() implements Packet {
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
			(DimensionType)buf.decode(DimensionType.REGISTRY_CODEC).get(),
			RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier()),
			buf.readLong(),
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
		boolean reducedDebugInfo,
		boolean showDeathScreen,
		boolean debugWorld,
		boolean flatWorld
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
		this.reducedDebugInfo = reducedDebugInfo;
		this.showDeathScreen = showDeathScreen;
		this.debugWorld = debugWorld;
		this.flatWorld = flatWorld;
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
}
