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
	public NetherForestVegetationFeature(Function<Dynamic<?>, ? extends BlockPileFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, BlockPileFeatureConfig blockPileFeatureConfig
	) {
		for (Block block = iWorld.getBlockState(blockPos.down()).getBlock();
			!block.isIn(BlockTags.NYLIUM) && blockPos.getY() > 0;
			block = iWorld.getBlockState(blockPos).getBlock()
		) {
			blockPos = blockPos.down();
		}

		int i = blockPos.getY();
		if (i >= 1 && i + 1 < 256) {
			int j = 0;

			for (int k = 0; k < 64; k++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
				BlockState blockState = blockPileFeatureConfig.stateProvider.getBlockState(random, blockPos2);
				if (iWorld.isAir(blockPos2) && blockPos2.getY() > 0 && blockState.canPlaceAt(iWorld, blockPos2)) {
					iWorld.setBlockState(blockPos2, blockState, 2);
					j++;
				}
			}

			return j > 0;
		} else {
			return false;
		}
	}
}
