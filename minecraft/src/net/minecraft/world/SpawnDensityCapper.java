package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.util.math.ChunkPos;

public class SpawnDensityCapper {
	private final Long2ObjectMap<List<ServerPlayerEntity>> chunkPosToMobSpawnablePlayers = new Long2ObjectOpenHashMap<>();
	private final Map<ServerPlayerEntity, SpawnDensityCapper.DensityCap> playersToDensityCap = Maps.<ServerPlayerEntity, SpawnDensityCapper.DensityCap>newHashMap();
	private final ServerChunkLoadingManager chunkLoadingManager;

	public SpawnDensityCapper(ServerChunkLoadingManager chunkLoadingManager) {
		this.chunkLoadingManager = chunkLoadingManager;
	}

	private List<ServerPlayerEntity> getMobSpawnablePlayers(ChunkPos chunkPos) {
		return this.chunkPosToMobSpawnablePlayers
			.computeIfAbsent(
				chunkPos.toLong(), (Long2ObjectFunction<? extends List<ServerPlayerEntity>>)(pos -> this.chunkLoadingManager.getPlayersWatchingChunk(chunkPos))
			);
	}

	public void increaseDensity(ChunkPos chunkPos, SpawnGroup spawnGroup) {
		for (ServerPlayerEntity serverPlayerEntity : this.getMobSpawnablePlayers(chunkPos)) {
			((SpawnDensityCapper.DensityCap)this.playersToDensityCap.computeIfAbsent(serverPlayerEntity, player -> new SpawnDensityCapper.DensityCap()))
				.increaseDensity(spawnGroup);
		}
	}

	public boolean canSpawn(SpawnGroup spawnGroup, ChunkPos chunkPos) {
		for (ServerPlayerEntity serverPlayerEntity : this.getMobSpawnablePlayers(chunkPos)) {
			SpawnDensityCapper.DensityCap densityCap = (SpawnDensityCapper.DensityCap)this.playersToDensityCap.get(serverPlayerEntity);
			if (densityCap == null || densityCap.canSpawn(spawnGroup)) {
				return true;
			}
		}

		return false;
	}

	static class DensityCap {
		private final Object2IntMap<SpawnGroup> spawnGroupsToDensity = new Object2IntOpenHashMap<>(SpawnGroup.values().length);

		public void increaseDensity(SpawnGroup spawnGroup) {
			this.spawnGroupsToDensity.computeInt(spawnGroup, (group, density) -> density == null ? 1 : density + 1);
		}

		public boolean canSpawn(SpawnGroup spawnGroup) {
			return this.spawnGroupsToDensity.getOrDefault(spawnGroup, 0) < spawnGroup.getCapacity();
		}
	}
}
