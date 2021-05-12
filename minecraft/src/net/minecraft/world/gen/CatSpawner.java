package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class CatSpawner implements Spawner {
	private static final int SPAWN_COOLDOWN = 1200;
	private int ticksUntilNextSpawn;

	@Override
	public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
		if (spawnAnimals && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
			this.ticksUntilNextSpawn--;
			if (this.ticksUntilNextSpawn > 0) {
				return 0;
			} else {
				this.ticksUntilNextSpawn = 1200;
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

							if (world.getStructureAccessor().getStructureAt(blockPos, true, StructureFeature.SWAMP_HUT).hasChildren()) {
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

	private int spawnInHouse(ServerWorld world, BlockPos pos) {
		int i = 48;
		if (world.getPointOfInterestStorage().count(PointOfInterestType.HOME.getCompletionCondition(), pos, 48, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED)
			> 4L) {
			List<CatEntity> list = world.getNonSpectatingEntities(CatEntity.class, new Box(pos).expand(48.0, 8.0, 48.0));
			if (list.size() < 5) {
				return this.spawn(pos, world);
			}
		}

		return 0;
	}

	private int spawnInSwampHut(ServerWorld world, BlockPos pos) {
		int i = 16;
		List<CatEntity> list = world.getNonSpectatingEntities(CatEntity.class, new Box(pos).expand(16.0, 8.0, 16.0));
		return list.size() < 1 ? this.spawn(pos, world) : 0;
	}

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
