package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.entity.EntityLike;

public class SpawnDensityCapper {
	final Long2ObjectMap<List<EntityLike>> chunkPosToMobSpawnablePlayers = new Long2ObjectOpenHashMap<>();
	final HashMap<EntityLike, SpawnDensityCapper.DensityCap> playersToDensityCap = Maps.newHashMap();
	private final ThreadedAnvilChunkStorage threadedAnvilChunkStorage;
	private final ServerWorld world;

	public SpawnDensityCapper(ThreadedAnvilChunkStorage threadedAnvilChunkStorage, ServerWorld world) {
		this.threadedAnvilChunkStorage = threadedAnvilChunkStorage;
		this.world = world;
	}

	private List<EntityLike> getMobSpawnablePlayers(long chunkPos) {
		return this.chunkPosToMobSpawnablePlayers
			.computeIfAbsent(chunkPos, chunkPosx -> this.threadedAnvilChunkStorage.getMobSpawnablePlayers(new ChunkPos(chunkPosx)));
	}

	public void increaseDensity(long chunkPos, SpawnGroup spawnGroup) {
		List<EntityLike> list = this.getMobSpawnablePlayers(chunkPos);
		float f = 1.0F / (float)spawnGroup.getCapacity();

		for (EntityLike entityLike : list) {
			SpawnDensityCapper.DensityCap densityCap = (SpawnDensityCapper.DensityCap)this.playersToDensityCap
				.computeIfAbsent(entityLike, player -> new SpawnDensityCapper.DensityCap());
			densityCap.increaseDensity(spawnGroup, f);
		}
	}

	public boolean canSpawn(SpawnGroup spawnGroup, ChunkPos chunkPos) {
		return this.getMobSpawnablePlayers(chunkPos.toLong()).stream().anyMatch(player -> this.canSpawn(player, spawnGroup));
	}

	private boolean canSpawn(EntityLike player, SpawnGroup spawnGroup) {
		SpawnDensityCapper.DensityCap densityCap = (SpawnDensityCapper.DensityCap)this.playersToDensityCap.get(player);
		return densityCap == null || densityCap.canSpawn(spawnGroup);
	}

	class DensityCap {
		private final Object2FloatMap<SpawnGroup> spawnGroupsToDensity = new Object2FloatOpenHashMap<>(SpawnGroup.values().length);

		public void increaseDensity(SpawnGroup spawnGroup, float delta) {
			float f = this.spawnGroupsToDensity.getOrDefault(spawnGroup, 0.0F);
			this.spawnGroupsToDensity.put(spawnGroup, f + delta);
		}

		public boolean canSpawn(SpawnGroup spawnGroup) {
			return this.spawnGroupsToDensity.getOrDefault(spawnGroup, 0.0F) < 1.0F;
		}
	}
}
