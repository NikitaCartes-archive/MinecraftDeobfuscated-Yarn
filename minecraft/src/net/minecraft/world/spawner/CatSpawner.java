package net.minecraft.world.spawner;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

/**
 * A spawner for cats in villages and swamp huts.
 * 
 * @implNote Cats in swamp huts are also spawned in
 * {@link net.minecraft.world.gen.chunk.ChunkGenerator#getEntitySpawnList}.
 */
public class CatSpawner implements Spawner {
	private static final int SPAWN_INTERVAL = 1200;
	private int cooldown;

	@Override
	public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
		if (spawnAnimals && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
			this.cooldown--;
			if (this.cooldown > 0) {
				return 0;
			} else {
				this.cooldown = 1200;
				PlayerEntity playerEntity = world.getRandomAlivePlayer();
				if (playerEntity == null) {
					return 0;
				} else {
					Random random = world.random;
					int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
					int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
					BlockPos blockPos = playerEntity.getBlockPos().add(i, 0, j);
					int k = 10;
					if (!world.isRegionLoaded(blockPos.getX() - 10, blockPos.getZ() - 10, blockPos.getX() + 10, blockPos.getZ() + 10)) {
						return 0;
					} else {
						if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, blockPos, EntityType.CAT)) {
							if (world.isNearOccupiedPointOfInterest(blockPos, 2)) {
								return this.spawnInHouse(world, blockPos);
							}

							if (world.getStructureAccessor().getStructureContaining(blockPos, StructureTags.CATS_SPAWN_IN).hasChildren()) {
								return this.spawnInSwampHut(world, blockPos);
							}
						}

						return 0;
					}
				}
			}
		} else {
			return 0;
		}
	}

	/**
	 * Tries to spawn cats in villages.
	 * 
	 * @return the number of cats spawned
	 * 
	 * @implNote Cats spawn when there are more than 5 occupied beds and less than 5 existing cats.
	 */
	private int spawnInHouse(ServerWorld world, BlockPos pos) {
		int i = 48;
		if (world.getPointOfInterestStorage()
				.count(entry -> entry.matchesKey(PointOfInterestTypes.HOME), pos, 48, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED)
			> 4L) {
			List<CatEntity> list = world.getNonSpectatingEntities(CatEntity.class, new Box(pos).expand(48.0, 8.0, 48.0));
			if (list.size() < 5) {
				return this.spawn(pos, world);
			}
		}

		return 0;
	}

	/**
	 * Tries to spawn cats in swamp huts.
	 * 
	 * @return the number of cats spawned
	 */
	private int spawnInSwampHut(ServerWorld world, BlockPos pos) {
		int i = 16;
		List<CatEntity> list = world.getNonSpectatingEntities(CatEntity.class, new Box(pos).expand(16.0, 8.0, 16.0));
		return list.size() < 1 ? this.spawn(pos, world) : 0;
	}

	/**
	 * Spawns a cat.
	 * 
	 * @return the number of cats spawned
	 */
	private int spawn(BlockPos pos, ServerWorld world) {
		CatEntity catEntity = EntityType.CAT.create(world);
		if (catEntity == null) {
			return 0;
		} else {
			catEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.NATURAL, null, null);
			catEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
			world.spawnEntityAndPassengers(catEntity);
			return 1;
		}
	}
}
