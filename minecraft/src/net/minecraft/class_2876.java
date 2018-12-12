package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.EndPillarFeatureConfig;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.Feature;

public enum class_2876 {
	field_13097 {
		@Override
		public void method_12507(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos) {
			BlockPos blockPos2 = new BlockPos(0, 128, 0);

			for (EnderCrystalEntity enderCrystalEntity : list) {
				enderCrystalEntity.setBeamTarget(blockPos2);
			}

			enderDragonFight.method_12521(field_13095);
		}
	},
	field_13095 {
		@Override
		public void method_12507(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos) {
			if (i < 100) {
				if (i == 0 || i == 50 || i == 51 || i == 52 || i >= 95) {
					serverWorld.fireWorldEvent(3001, new BlockPos(0, 128, 0), 0);
				}
			} else {
				enderDragonFight.method_12521(field_13094);
			}
		}
	},
	field_13094 {
		@Override
		public void method_12507(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos) {
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

						for (BlockPos.Mutable mutable : BlockPos.iterateBoxPositionsMutable(
							new BlockPos(spike.getCenterX() - 10, spike.getHeight() - 10, spike.getCenterZ() - 10),
							new BlockPos(spike.getCenterX() + 10, spike.getHeight() + 10, spike.getCenterZ() + 10)
						)) {
							serverWorld.clearBlockState(mutable);
						}

						serverWorld.createExplosion(
							null, (double)((float)spike.getCenterX() + 0.5F), (double)spike.getHeight(), (double)((float)spike.getCenterZ() + 0.5F), 5.0F, true
						);
						EndPillarFeatureConfig endPillarFeatureConfig = new EndPillarFeatureConfig(true, ImmutableList.of(spike), new BlockPos(0, 128, 0));
						Feature.field_13522
							.method_13151(
								serverWorld,
								(ChunkGenerator<? extends ChunkGeneratorConfig>)serverWorld.getChunkManager().getChunkGenerator(),
								new Random(),
								new BlockPos(spike.getCenterX(), 45, spike.getCenterZ()),
								endPillarFeatureConfig
							);
					}
				} else if (bl) {
					enderDragonFight.method_12521(field_13098);
				}
			}
		}
	},
	field_13098 {
		@Override
		public void method_12507(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos) {
			if (i >= 100) {
				enderDragonFight.method_12521(field_13099);
				enderDragonFight.method_12524();

				for (EnderCrystalEntity enderCrystalEntity : list) {
					enderCrystalEntity.setBeamTarget(null);
					serverWorld.createExplosion(enderCrystalEntity, enderCrystalEntity.x, enderCrystalEntity.y, enderCrystalEntity.z, 6.0F, false);
					enderCrystalEntity.invalidate();
				}
			} else if (i >= 80) {
				serverWorld.fireWorldEvent(3001, new BlockPos(0, 128, 0), 0);
			} else if (i == 0) {
				for (EnderCrystalEntity enderCrystalEntity : list) {
					enderCrystalEntity.setBeamTarget(new BlockPos(0, 128, 0));
				}
			} else if (i < 5) {
				serverWorld.fireWorldEvent(3001, new BlockPos(0, 128, 0), 0);
			}
		}
	},
	field_13099 {
		@Override
		public void method_12507(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos) {
		}
	};

	private class_2876() {
	}

	public abstract void method_12507(ServerWorld serverWorld, EnderDragonFight enderDragonFight, List<EnderCrystalEntity> list, int i, BlockPos blockPos);
}
