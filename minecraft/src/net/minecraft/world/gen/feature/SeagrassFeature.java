package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class SeagrassFeature extends Feature<SeagrassFeatureConfig> {
	public SeagrassFeature(Function<Dynamic<?>, ? extends SeagrassFeatureConfig> function) {
		super(function);
	}

	public boolean method_13926(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, SeagrassFeatureConfig seagrassFeatureConfig
	) {
		int i = 0;

		for (int j = 0; j < seagrassFeatureConfig.count; j++) {
			int k = random.nextInt(8) - random.nextInt(8);
			int l = random.nextInt(8) - random.nextInt(8);
			int m = iWorld.getTop(Heightmap.Type.field_13200, blockPos.getX() + k, blockPos.getZ() + l);
			BlockPos blockPos2 = new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l);
			if (iWorld.getBlockState(blockPos2).getBlock() == Blocks.field_10382) {
				boolean bl = random.nextDouble() < seagrassFeatureConfig.tallSeagrassProbability;
				BlockState blockState = bl ? Blocks.field_10238.getDefaultState() : Blocks.field_10376.getDefaultState();
				if (blockState.canPlaceAt(iWorld, blockPos2)) {
					if (bl) {
						BlockState blockState2 = blockState.with(TallSeagrassBlock.HALF, DoubleBlockHalf.field_12609);
						BlockPos blockPos3 = blockPos2.up();
						if (iWorld.getBlockState(blockPos3).getBlock() == Blocks.field_10382) {
							iWorld.setBlockState(blockPos2, blockState, 2);
							iWorld.setBlockState(blockPos3, blockState2, 2);
						}
					} else {
						iWorld.setBlockState(blockPos2, blockState, 2);
					}

					i++;
				}
			}
		}

		return i > 0;
	}
}
