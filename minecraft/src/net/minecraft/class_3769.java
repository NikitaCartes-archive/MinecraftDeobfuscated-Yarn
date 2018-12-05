package net.minecraft;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;

public class class_3769 {
	private static final List<class_3769.class_3731> field_16651 = Arrays.asList(
		new class_3769.class_3731(EntityType.PILLAGER, 80), new class_3769.class_3731(EntityType.VINDICATOR, 20)
	);
	private int field_16652;

	public int spawn(World world, boolean bl, boolean bl2) {
		if (!bl) {
			return 0;
		} else {
			Random random = world.random;
			this.field_16652--;
			if (this.field_16652 > 0) {
				return 0;
			} else {
				this.field_16652 = this.field_16652 + 6000 + random.nextInt(1200);
				long l = world.getTimeOfDay() / 24000L;
				if (l < 5L || !world.isDaylight()) {
					return 0;
				} else if (random.nextInt(5) != 0) {
					return 0;
				} else {
					int i = world.players.size();
					if (i < 1) {
						return 0;
					} else {
						PlayerEntity playerEntity = (PlayerEntity)world.players.get(random.nextInt(i));
						if (playerEntity.isSpectator()) {
							return 0;
						} else {
							int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
							int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
							BlockPos blockPos = new BlockPos(playerEntity).add(j, 0, k);
							if (!world.isAreaLoaded(
								blockPos.getX() - 10, blockPos.getY() - 10, blockPos.getZ() - 10, blockPos.getX() + 10, blockPos.getY() + 10, blockPos.getZ() + 10
							)) {
								return 0;
							} else {
								Biome biome = world.getBiome(blockPos);
								Biome.Category category = biome.getCategory();
								if (category != Biome.Category.PLAINS && category != Biome.Category.TAIGA && category != Biome.Category.DESERT && category != Biome.Category.SAVANNA) {
									return 0;
								} else {
									int m = 1;
									this.method_16575(world, blockPos, random, true);
									int n = (int)Math.ceil((double)world.getLocalDifficulty(blockPos).method_5457());

									for (int o = 0; o < n; o++) {
										m++;
										this.method_16575(world, blockPos, random, false);
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

	private void method_16575(World world, BlockPos blockPos, Random random, boolean bl) {
		class_3769.class_3731 lv = WeightedPicker.getRandom(random, field_16651);
		PatrolEntity patrolEntity = lv.field_16476.create(world);
		if (patrolEntity != null) {
			double d = (double)(blockPos.getX() + random.nextInt(5) - random.nextInt(5));
			double e = (double)(blockPos.getZ() + random.nextInt(5) - random.nextInt(5));
			BlockPos blockPos2 = patrolEntity.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(d, (double)blockPos.getY(), e));
			if (bl) {
				patrolEntity.method_16217(true);
				patrolEntity.method_16218();
			}

			patrolEntity.setPosition((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
			patrolEntity.method_5943(world, world.getLocalDifficulty(blockPos2), class_3730.field_16527, null, null);
			world.spawnEntity(patrolEntity);
		}
	}

	public static class class_3731 extends WeightedPicker.Entry {
		public final EntityType<? extends PatrolEntity> field_16476;

		public class_3731(EntityType<? extends PatrolEntity> entityType, int i) {
			super(i);
			this.field_16476 = entityType;
		}
	}
}
