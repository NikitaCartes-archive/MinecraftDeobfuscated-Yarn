package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;

public class PillagerSpawner implements Spawner {
	private int ticksUntilNextSpawn;

	@Override
	public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
		if (!spawnMonsters) {
			return 0;
		} else if (!world.getGameRules().getBoolean(GameRules.DO_PATROL_SPAWNING)) {
			return 0;
		} else {
			Random random = world.random;
			this.ticksUntilNextSpawn--;
			if (this.ticksUntilNextSpawn > 0) {
				return 0;
			} else {
				this.ticksUntilNextSpawn = this.ticksUntilNextSpawn + 12000 + random.nextInt(1200);
				long l = world.getTimeOfDay() / 24000L;
				if (l < 5L || !world.isDay()) {
					return 0;
				} else if (random.nextInt(5) != 0) {
					return 0;
				} else {
					int i = world.getPlayers().size();
					if (i < 1) {
						return 0;
					} else {
						PlayerEntity playerEntity = (PlayerEntity)world.getPlayers().get(random.nextInt(i));
						if (playerEntity.isSpectator()) {
							return 0;
						} else if (world.isNearOccupiedPointOfInterest(playerEntity.getBlockPos(), 2)) {
							return 0;
						} else {
							int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
							int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
							BlockPos.Mutable mutable = playerEntity.getBlockPos().mutableCopy().move(j, 0, k);
							if (!world.isRegionLoaded(mutable.getX() - 10, mutable.getY() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getY() + 10, mutable.getZ() + 10)) {
								return 0;
							} else {
								Biome biome = world.getBiome(mutable);
								Biome.Category category = biome.getCategory();
								if (category == Biome.Category.MUSHROOM) {
									return 0;
								} else {
									int m = 0;
									int n = (int)Math.ceil((double)world.getLocalDifficulty(mutable).getLocalDifficulty()) + 1;

									for (int o = 0; o < n; o++) {
										m++;
										mutable.setY(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
										if (o == 0) {
											if (!this.spawnPillager(world, mutable, random, true)) {
												break;
											}
										} else {
											this.spawnPillager(world, mutable, random, false);
										}

										mutable.setX(mutable.getX() + random.nextInt(5) - random.nextInt(5));
										mutable.setZ(mutable.getZ() + random.nextInt(5) - random.nextInt(5));
									}

									return m;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param captain whether the pillager is the captain of a patrol
	 */
	private boolean spawnPillager(ServerWorld world, BlockPos pos, Random random, boolean captain) {
		BlockState blockState = world.getBlockState(pos);
		if (!SpawnHelper.isClearForSpawn(world, pos, blockState, blockState.getFluidState(), EntityType.PILLAGER)) {
			return false;
		} else if (!PatrolEntity.canSpawn(EntityType.PILLAGER, world, SpawnReason.PATROL, pos, random)) {
			return false;
		} else {
			PatrolEntity patrolEntity = EntityType.PILLAGER.create(world);
			if (patrolEntity != null) {
				if (captain) {
					patrolEntity.setPatrolLeader(true);
					patrolEntity.setRandomPatrolTarget();
				}

				patrolEntity.setPosition((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
				patrolEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.PATROL, null, null);
				world.spawnEntityAndPassengers(patrolEntity);
				return true;
			} else {
				return false;
			}
		}
	}
}
