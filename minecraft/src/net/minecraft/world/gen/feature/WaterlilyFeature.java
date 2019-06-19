package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class WaterlilyFeature extends Feature<DefaultFeatureConfig> {
	public WaterlilyFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_14202(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockPos blockPos2 = blockPos;

		while (blockPos2.getY() > 0) {
			BlockPos blockPos3 = blockPos2.down();
			if (!iWorld.isAir(blockPos3)) {
				break;
			}

			blockPos2 = blockPos3;
		}

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos4 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			BlockState blockState = Blocks.field_10588.getDefaultState();
			if (iWorld.isAir(blockPos4) && blockState.canPlaceAt(iWorld, blockPos4)) {
				iWorld.setBlockState(blockPos4, blockState, 2);
			}
		}

		return true;
	}
}
