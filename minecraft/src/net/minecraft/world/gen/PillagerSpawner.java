package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class PillagerSpawner {
	private int ticksUntilNextSpawn;

	public int spawn(ServerWorld serverWorld, boolean bl, boolean bl2) {
		if (!bl) {
			return 0;
		} else {
			Random random = serverWorld.random;
			this.ticksUntilNextSpawn--;
			if (this.ticksUntilNextSpawn > 0) {
				return 0;
			} else {
				this.ticksUntilNextSpawn = this.ticksUntilNextSpawn + 12000 + random.nextInt(1200);
				long l = serverWorld.getTimeOfDay() / 24000L;
				if (l < 5L || !serverWorld.isDaylight()) {
					return 0;
				} else if (random.nextInt(5) != 0) {
					return 0;
				} else {
					int i = serverWorld.getPlayers().size();
					if (i < 1) {
						return 0;
					} else {
						PlayerEntity playerEntity = (PlayerEntity)serverWorld.getPlayers().get(random.nextInt(i));
						if (playerEntity.isSpectator()) {
							return 0;
						} else if (serverWorld.isNearOccupiedPointOfInterest(playerEntity.getBlockPos())) {
							return 0;
						} else {
							int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
							int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
							BlockPos.Mutable mutable = new BlockPos.Mutable();
							mutable.set(playerEntity.x, playerEntity.y, playerEntity.z).setOffset(j, 0, k);
							if (!serverWorld.isAreaLoaded(
								mutable.getX() - 10, mutable.getY() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getY() + 10, mutable.getZ() + 10
							)) {
								return 0;
							} else {
								Biome biome = serverWorld.getBiome(mutable);
								Biome.Category category = biome.getCategory();
								if (category == Biome.Category.field_9365) {
									return 0;
								} else {
									int m = 0;
									int n = (int)Math.ceil((double)serverWorld.getLocalDifficulty(mutable).getLocalDifficulty()) + 1;

									for (int o = 0; o < n; o++) {
										m++;
										mutable.setY(serverWorld.getTopPosition(Heightmap.Type.field_13203, mutable).getY());
										if (o == 0) {
											if (!this.spawnOneEntity(serverWorld, mutable, random, true)) {
												break;
											}
										} else {
											this.spawnOneEntity(serverWorld, mutable, random, false);
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

	private boolean spawnOneEntity(World world, BlockPos blockPos, Random random, boolean bl) {
		if (!PatrolEntity.method_20739(EntityType.field_6105, world, SpawnType.field_16527, blockPos, random)) {
			return false;
		} else {
			PatrolEntity patrolEntity = EntityType.field_6105.create(world);
			if (patrolEntity != null) {
				if (bl) {
					patrolEntity.setPatrolLeader(true);
					patrolEntity.setRandomPatrolTarget();
				}

				patrolEntity.setPosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
				patrolEntity.initialize(world, world.getLocalDifficulty(blockPos), SpawnType.field_16527, null, null);
				world.spawnEntity(patrolEntity);
				return true;
			} else {
				return false;
			}
		}
	}
}
