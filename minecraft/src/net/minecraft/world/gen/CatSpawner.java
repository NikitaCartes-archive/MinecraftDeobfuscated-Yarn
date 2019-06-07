package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;

public class CatSpawner {
	private int ticksUntilNextSpawn;

	public int spawn(ServerWorld serverWorld, boolean bl, boolean bl2) {
		if (bl2 && serverWorld.getGameRules().getBoolean(GameRules.field_19390)) {
			this.ticksUntilNextSpawn--;
			if (this.ticksUntilNextSpawn > 0) {
				return 0;
			} else {
				this.ticksUntilNextSpawn = 1200;
				PlayerEntity playerEntity = serverWorld.getRandomAlivePlayer();
				if (playerEntity == null) {
					return 0;
				} else {
					Random random = serverWorld.random;
					int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
					int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
					BlockPos blockPos = new BlockPos(playerEntity).add(i, 0, j);
					if (!serverWorld.isAreaLoaded(
						blockPos.getX() - 10, blockPos.getY() - 10, blockPos.getZ() - 10, blockPos.getX() + 10, blockPos.getY() + 10, blockPos.getZ() + 10
					)) {
						return 0;
					} else {
						if (SpawnHelper.canSpawn(SpawnRestriction.Location.field_6317, serverWorld, blockPos, EntityType.field_16281)) {
							if (serverWorld.isNearOccupiedPointOfInterest(blockPos, 2)) {
								return this.spawnInHouse(serverWorld, blockPos);
							}

							if (Feature.SWAMP_HUT.isInsideStructure(serverWorld, blockPos)) {
								return this.spawnInSwampHut(serverWorld, blockPos);
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

	private int spawnInHouse(ServerWorld serverWorld, BlockPos blockPos) {
		int i = 48;
		if (serverWorld.getPointOfInterestStorage()
				.count(PointOfInterestType.field_18517.getCompletionCondition(), blockPos, 48, PointOfInterestStorage.OccupationStatus.field_18488)
			> 4L) {
			List<CatEntity> list = serverWorld.getEntities(CatEntity.class, new Box(blockPos).expand(48.0, 8.0, 48.0));
			if (list.size() < 5) {
				return this.spawn(blockPos, serverWorld);
			}
		}

		return 0;
	}

	private int spawnInSwampHut(World world, BlockPos blockPos) {
		int i = 16;
		List<CatEntity> list = world.getEntities(CatEntity.class, new Box(blockPos).expand(16.0, 8.0, 16.0));
		return list.size() < 1 ? this.spawn(blockPos, world) : 0;
	}

	private int spawn(BlockPos blockPos, World world) {
		CatEntity catEntity = EntityType.field_16281.create(world);
		if (catEntity == null) {
			return 0;
		} else {
			catEntity.initialize(world, world.getLocalDifficulty(blockPos), SpawnType.field_16459, null, null);
			catEntity.setPositionAndAngles(blockPos, 0.0F, 0.0F);
			world.spawnEntity(catEntity);
			return 1;
		}
	}
}
