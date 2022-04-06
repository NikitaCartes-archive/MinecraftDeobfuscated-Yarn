package net.minecraft.world.spawner;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;

/**
 * A spawner for pillager patrols.
 * 
 * <p>Pillager spawns in pillager outposts are controlled at
 * {@link net.minecraft.world.gen.chunk.ChunkGenerator#getEntitySpawnList}.
 */
public class PatrolSpawner implements Spawner {
	private int cooldown;

	@Override
	public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
		if (!spawnMonsters) {
			return 0;
		} else if (!world.getGameRules().getBoolean(GameRules.DO_PATROL_SPAWNING)) {
			return 0;
		} else {
			AbstractRandom abstractRandom = world.random;
			this.cooldown--;
			if (this.cooldown > 0) {
				return 0;
			} else {
				this.cooldown = this.cooldown + 12000 + abstractRandom.nextInt(1200);
				long l = world.getTimeOfDay() / 24000L;
				if (l < 5L || !world.isDay()) {
					return 0;
				} else if (abstractRandom.nextInt(5) != 0) {
					return 0;
				} else {
					int i = world.getPlayers().size();
					if (i < 1) {
						return 0;
					} else {
						PlayerEntity playerEntity = (PlayerEntity)world.getPlayers().get(abstractRandom.nextInt(i));
						if (playerEntity.isSpectator()) {
							return 0;
						} else if (world.isNearOccupiedPointOfInterest(playerEntity.getBlockPos(), 2)) {
							return 0;
						} else {
							int j = (24 + abstractRandom.nextInt(24)) * (abstractRandom.nextBoolean() ? -1 : 1);
							int k = (24 + abstractRandom.nextInt(24)) * (abstractRandom.nextBoolean() ? -1 : 1);
							BlockPos.Mutable mutable = playerEntity.getBlockPos().mutableCopy().move(j, 0, k);
							int m = 10;
							if (!world.isRegionLoaded(mutable.getX() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getZ() + 10)) {
								return 0;
							} else {
								RegistryEntry<Biome> registryEntry = world.getBiome(mutable);
								if (registryEntry.isIn(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
									return 0;
								} else {
									int n = 0;
									int o = (int)Math.ceil((double)world.getLocalDifficulty(mutable).getLocalDifficulty()) + 1;

									for (int p = 0; p < o; p++) {
										n++;
										mutable.setY(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
										if (p == 0) {
											if (!this.spawnPillager(world, mutable, abstractRandom, true)) {
												break;
											}
										} else {
											this.spawnPillager(world, mutable, abstractRandom, false);
										}

										mutable.setX(mutable.getX() + abstractRandom.nextInt(5) - abstractRandom.nextInt(5));
										mutable.setZ(mutable.getZ() + abstractRandom.nextInt(5) - abstractRandom.nextInt(5));
									}

									return n;
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
	private boolean spawnPillager(ServerWorld world, BlockPos pos, AbstractRandom random, boolean captain) {
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
