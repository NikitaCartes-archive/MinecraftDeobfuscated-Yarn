package net.minecraft.entity.boss.dragon;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public enum EnderDragonSpawnState {
	START {
		@Override
		public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
			BlockPos blockPos = new BlockPos(0, 128, 0);

			for (EndCrystalEntity endCrystalEntity : crystals) {
				endCrystalEntity.setBeamTarget(blockPos);
			}

			fight.setSpawnState(PREPARING_TO_SUMMON_PILLARS);
		}
	},
	PREPARING_TO_SUMMON_PILLARS {
		@Override
		public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
			if (tick < 100) {
				if (tick == 0 || tick == 50 || tick == 51 || tick == 52 || tick >= 95) {
					world.syncWorldEvent(WorldEvents.ENDER_DRAGON_RESURRECTED, new BlockPos(0, 128, 0), 0);
				}
			} else {
				fight.setSpawnState(SUMMONING_PILLARS);
			}
		}
	},
	SUMMONING_PILLARS {
		@Override
		public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
			int i = 40;
			boolean bl = tick % 40 == 0;
			boolean bl2 = tick % 40 == 39;
			if (bl || bl2) {
				List<EndSpikeFeature.Spike> list = EndSpikeFeature.getSpikes(world);
				int j = tick / 40;
				if (j < list.size()) {
					EndSpikeFeature.Spike spike = (EndSpikeFeature.Spike)list.get(j);
					if (bl) {
						for (EndCrystalEntity endCrystalEntity : crystals) {
							endCrystalEntity.setBeamTarget(new BlockPos(spike.getCenterX(), spike.getHeight() + 1, spike.getCenterZ()));
						}
					} else {
						int k = 10;

						for (BlockPos blockPos : BlockPos.iterate(
							new BlockPos(spike.getCenterX() - 10, spike.getHeight() - 10, spike.getCenterZ() - 10),
							new BlockPos(spike.getCenterX() + 10, spike.getHeight() + 10, spike.getCenterZ() + 10)
						)) {
							world.removeBlock(blockPos, false);
						}

						world.createExplosion(
							null,
							(double)((float)spike.getCenterX() + 0.5F),
							(double)spike.getHeight(),
							(double)((float)spike.getCenterZ() + 0.5F),
							5.0F,
							World.ExplosionSourceType.BLOCK
						);
						EndSpikeFeatureConfig endSpikeFeatureConfig = new EndSpikeFeatureConfig(true, ImmutableList.of(spike), new BlockPos(0, 128, 0));
						Feature.END_SPIKE
							.generateIfValid(
								endSpikeFeatureConfig, world, world.getChunkManager().getChunkGenerator(), Random.create(), new BlockPos(spike.getCenterX(), 45, spike.getCenterZ())
							);
					}
				} else if (bl) {
					fight.setSpawnState(SUMMONING_DRAGON);
				}
			}
		}
	},
	SUMMONING_DRAGON {
		@Override
		public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
			if (tick >= 100) {
				fight.setSpawnState(END);
				fight.resetEndCrystals();

				for (EndCrystalEntity endCrystalEntity : crystals) {
					endCrystalEntity.setBeamTarget(null);
					world.createExplosion(endCrystalEntity, endCrystalEntity.getX(), endCrystalEntity.getY(), endCrystalEntity.getZ(), 6.0F, World.ExplosionSourceType.NONE);
					endCrystalEntity.discard();
				}
			} else if (tick >= 80) {
				world.syncWorldEvent(WorldEvents.ENDER_DRAGON_RESURRECTED, new BlockPos(0, 128, 0), 0);
			} else if (tick == 0) {
				for (EndCrystalEntity endCrystalEntity : crystals) {
					endCrystalEntity.setBeamTarget(new BlockPos(0, 128, 0));
				}
			} else if (tick < 5) {
				world.syncWorldEvent(WorldEvents.ENDER_DRAGON_RESURRECTED, new BlockPos(0, 128, 0), 0);
			}
		}
	},
	END {
		@Override
		public void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos) {
		}
	};

	public abstract void run(ServerWorld world, EnderDragonFight fight, List<EndCrystalEntity> crystals, int tick, BlockPos pos);
}
