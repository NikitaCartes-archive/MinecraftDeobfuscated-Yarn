package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SeaPickleFeature extends Feature<SeaPickleFeatureConfig> {
	public SeaPickleFeature(Function<Dynamic<?>, ? extends SeaPickleFeatureConfig> function) {
		super(function);
	}

	public boolean method_13876(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockPos blockPos, SeaPickleFeatureConfig seaPickleFeatureConfig) {
		int i = 0;

		for (int j = 0; j < seaPickleFeatureConfig.count; j++) {
			int k = random.nextInt(8) - random.nextInt(8);
			int l = random.nextInt(8) - random.nextInt(8);
			int m = iWorld.method_8589(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + k, blockPos.getZ() + l);
			BlockPos blockPos2 = new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l);
			BlockState blockState = Blocks.field_10476.method_9564().method_11657(SeaPickleBlock.field_11472, Integer.valueOf(random.nextInt(4) + 1));
			if (iWorld.method_8320(blockPos2).getBlock() == Blocks.field_10382 && blockState.method_11591(iWorld, blockPos2)) {
				iWorld.method_8652(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}
}
