package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.feature.BlockClusterFeatureConfig;

public class DiskFeature extends Feature<BlockClusterFeatureConfig> {
	public DiskFeature(Function<Dynamic<?>, ? extends BlockClusterFeatureConfig> function) {
		super(function);
	}

	public boolean method_13005(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator,
		Random random,
		BlockPos blockPos,
		BlockClusterFeatureConfig blockClusterFeatureConfig
	) {
		if (!iWorld.getFluidState(blockPos).matches(FluidTags.field_15517)) {
			return false;
		} else {
			int i = 0;
			int j = random.nextInt(blockClusterFeatureConfig.radius - 2) + 2;

			for (int k = blockPos.getX() - j; k <= blockPos.getX() + j; k++) {
				for (int l = blockPos.getZ() - j; l <= blockPos.getZ() + j; l++) {
					int m = k - blockPos.getX();
					int n = l - blockPos.getZ();
					if (m * m + n * n <= j * j) {
						for (int o = blockPos.getY() - blockClusterFeatureConfig.ySize; o <= blockPos.getY() + blockClusterFeatureConfig.ySize; o++) {
							BlockPos blockPos2 = new BlockPos(k, o, l);
							BlockState blockState = iWorld.getBlockState(blockPos2);

							for (BlockState blockState2 : blockClusterFeatureConfig.targets) {
								if (blockState2.getBlock() == blockState.getBlock()) {
									iWorld.setBlockState(blockPos2, blockClusterFeatureConfig.state, 2);
									i++;
									break;
								}
							}
						}
					}
				}
			}

			return i > 0;
		}
	}
}
