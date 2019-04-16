package net.minecraft.entity.boss.dragon;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public enum EnderDragonSpawnState {
	START {
		@Override
		public void run(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos) {
			BlockPos blockPos2 = new BlockPos(0, 128, 0);

			for (EnderCrystalEntity enderCrystalEntity : list) {
				enderCrystalEntity.setBeamTarget(blockPos2);
			}

			enderDragonFight.setSpawnState(PREPARE_CREATE_SPIKES);
		}
	},
	PREPARE_CREATE_SPIKES {
		@Override
		public void run(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos) {
			if (i < 100) {
				if (i == 0 || i == 50 || i == 51 || i == 52 || i >= 95) {
					serverWorld.playLevelEvent(3001, new BlockPos(0, 128, 0), 0);
				}
			} else {
				enderDragonFight.setSpawnState(CREATE_SPIKES);
			}
		}
	},
	CREATE_SPIKES {
		@Override
		public void run(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos) {
			int j = 40;
			boolean bl = i % 40 == 0;
			boolean bl2 = i % 40 == 39;
			if (bl || bl2) {
				List<EndSpikeFeature.Spike> list2 = EndSpikeFeature.getSpikes(serverWorld);
				int k = i / 40;
				if (k < list2.size()) {
					EndSpikeFeature.Spike spike = (EndSpikeFeature.Spike)list2.get(k);
					if (bl) {
						for (EnderCrystalEntity enderCrystalEntity : list) {
							enderCrystalEntity.setBeamTarget(new BlockPos(spike.getCenterX(), spike.getHeight() + 1, spike.getCenterZ()));
						}
					} else {
						int l = 10;

						for (BlockPos blockPos2 : BlockPos.iterate(
							new BlockPos(spike.getCenterX() - 10, spike.getHeight() - 10, spike.getCenterZ() - 10),
							new BlockPos(spike.getCenterX() + 10, spike.getHeight() + 10, spike.getCenterZ() + 10)
						)) {
							serverWorld.clearBlockState(blockPos2, false);
						}

						serverWorld.createExplosion(
							null,
							(double)((float)spike.getCenterX() + 0.5F),
							(double)spike.getHeight(),
							(double)((float)spike.getCenterZ() + 0.5F),
							5.0F,
							Explosion.DestructionType.field_18687
						);
						EndSpikeFeatureConfig endSpikeFeatureConfig = new EndSpikeFeatureConfig(true, ImmutableList.of(spike), new BlockPos(0, 128, 0));
						Feature.field_13522
							.generate(
								serverWorld,
								(ChunkGenerator<? extends ChunkGeneratorConfig>)serverWorld.method_14178().getChunkGenerator(),
								new Random(),
								new BlockPos(spike.getCenterX(), 45, spike.getCenterZ()),
								endSpikeFeatureConfig
							);
					}
				} else if (bl) {
					enderDragonFight.setSpawnState(SPAWN_DRAGON);
				}
			}
		}
	},
	SPAWN_DRAGON {
		@Override
		public void run(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos) {
			if (i >= 100) {
				enderDragonFight.setSpawnState(END);
				enderDragonFight.resetEndCrystals();

				for (EnderCrystalEntity enderCrystalEntity : list) {
					enderCrystalEntity.setBeamTarget(null);
					serverWorld.createExplosion(
						enderCrystalEntity, enderCrystalEntity.x, enderCrystalEntity.y, enderCrystalEntity.z, 6.0F, Explosion.DestructionType.field_18685
					);
					enderCrystalEntity.remove();
				}
			} else if (i >= 80) {
				serverWorld.playLevelEvent(3001, new BlockPos(0, 128, 0), 0);
			} else if (i == 0) {
				for (EnderCrystalEntity enderCrystalEntity : list) {
					enderCrystalEntity.setBeamTarget(new BlockPos(0, 128, 0));
				}
			} else if (i < 5) {
				serverWorld.playLevelEvent(3001, new BlockPos(0, 128, 0), 0);
			}
		}
	},
	END {
		@Override
		public void run(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos) {
		}
	};

	private EnderDragonSpawnState() {
	}

	public abstract void run(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos);
}
