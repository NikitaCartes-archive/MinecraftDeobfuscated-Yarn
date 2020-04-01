package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class NetherForestVegetationFeature extends Feature<BlockPileFeatureConfig> {
	public NetherForestVegetationFeature(
		Function<Dynamic<?>, ? extends BlockPileFeatureConfig> function, Function<Random, ? extends BlockPileFeatureConfig> function2
	) {
		super(function, function2);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, BlockPileFeatureConfig blockPileFeatureConfig
	) {
		return method_26264(iWorld, random, blockPos, blockPileFeatureConfig, 8, 4);
	}

	public static boolean method_26264(IWorld iWorld, Random random, BlockPos blockPos, BlockPileFeatureConfig blockPileFeatureConfig, int i, int j) {
		for (Block block = iWorld.getBlockState(blockPos.down()).getBlock();
			!block.isIn(BlockTags.NYLIUM) && blockPos.getY() > 0;
			block = iWorld.getBlockState(blockPos).getBlock()
		) {
			blockPos = blockPos.down();
		}

		int k = blockPos.getY();
		if (k >= 1 && k + 1 < 256) {
			int l = 0;

			for (int m = 0; m < i * i; m++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
				BlockState blockState = blockPileFeatureConfig.stateProvider.getBlockState(random, blockPos2);
				if (iWorld.isAir(blockPos2) && blockPos2.getY() > 0 && blockState.canPlaceAt(iWorld, blockPos2)) {
					iWorld.setBlockState(blockPos2, blockState, 2);
					l++;
				}
			}

			return l > 0;
		} else {
			return false;
		}
	}
}
