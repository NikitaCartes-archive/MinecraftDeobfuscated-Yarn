package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
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
			int m = iWorld.method_8589(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + k, blockPos.getZ() + l);
			BlockPos blockPos2 = new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l);
			if (iWorld.method_8320(blockPos2).getBlock() == Blocks.field_10382) {
				boolean bl = random.nextDouble() < seagrassFeatureConfig.tallSeagrassProbability;
				BlockState blockState = bl ? Blocks.field_10238.method_9564() : Blocks.field_10376.method_9564();
				if (blockState.method_11591(iWorld, blockPos2)) {
					if (bl) {
						BlockState blockState2 = blockState.method_11657(TallSeagrassBlock.field_11616, DoubleBlockHalf.field_12609);
						BlockPos blockPos3 = blockPos2.up();
						if (iWorld.method_8320(blockPos3).getBlock() == Blocks.field_10382) {
							iWorld.method_8652(blockPos2, blockState, 2);
							iWorld.method_8652(blockPos3, blockState2, 2);
						}
					} else {
						iWorld.method_8652(blockPos2, blockState, 2);
					}

					i++;
				}
			}
		}

		return i > 0;
	}
}
