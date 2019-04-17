package net.minecraft.world.gen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class PillagerSpawner {
	private static final List<PillagerSpawner.SpawnEntry> SPAWN_ENTRIES = Arrays.asList(
		new PillagerSpawner.SpawnEntry(EntityType.PILLAGER, 80), new PillagerSpawner.SpawnEntry(EntityType.VINDICATOR, 20)
	);
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
				this.ticksUntilNextSpawn = this.ticksUntilNextSpawn + 6000 + random.nextInt(1200);
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
							BlockPos blockPos = new BlockPos(playerEntity).add(j, 0, k);
							if (!serverWorld.isAreaLoaded(
								blockPos.getX() - 10, blockPos.getY() - 10, blockPos.getZ() - 10, blockPos.getX() + 10, blockPos.getY() + 10, blockPos.getZ() + 10
							)) {
								return 0;
							} else {
								Biome biome = serverWorld.getBiome(blockPos);
								Biome.Category category = biome.getCategory();
								if (category != Biome.Category.PLAINS && category != Biome.Category.TAIGA && category != Biome.Category.DESERT && category != Biome.Category.SAVANNA) {
									return 0;
								} else {
									int m = 1;
									this.spawnOneEntity(serverWorld, blockPos, random, true);
									int n = (int)Math.ceil((double)serverWorld.getLocalDifficulty(blockPos).getLocalDifficulty());

									for (int o = 0; o < n; o++) {
										m++;
										this.spawnOneEntity(serverWorld, blockPos, random, false);
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

	private void spawnOneEntity(World world, BlockPos blockPos, Random random, boolean bl) {
		PillagerSpawner.SpawnEntry spawnEntry = WeightedPicker.getRandom(random, SPAWN_ENTRIES);
		PatrolEntity patrolEntity = spawnEntry.entityType.create(world);
		if (patrolEntity != null) {
			double d = (double)(blockPos.getX() + random.nextInt(5) - random.nextInt(5));
			double e = (double)(blockPos.getZ() + random.nextInt(5) - random.nextInt(5));
			BlockPos blockPos2 = patrolEntity.world.getTopPosition(Heightmap.Type.field_13203, new BlockPos(d, (double)blockPos.getY(), e));
			if (bl) {
				patrolEntity.setPatrolLeader(true);
				patrolEntity.setRandomPatrolTarget();
			}

			patrolEntity.setPosition((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
			patrolEntity.initialize(world, world.getLocalDifficulty(blockPos2), SpawnType.field_16527, null, null);
			world.spawnEntity(patrolEntity);
		}
	}

	public static class SpawnEntry extends WeightedPicker.Entry {
		public final EntityType<? extends PatrolEntity> entityType;

		public SpawnEntry(EntityType<? extends PatrolEntity> entityType, int i) {
			super(i);
			this.entityType = entityType;
		}
	}
}
