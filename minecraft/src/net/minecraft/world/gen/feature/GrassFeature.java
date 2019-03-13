package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class GrassFeature extends Feature<GrassFeatureConfig> {
	public GrassFeature(Function<Dynamic<?>, ? extends GrassFeatureConfig> function) {
		super(function);
	}

	public boolean method_14080(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, GrassFeatureConfig grassFeatureConfig
	) {
		for (BlockState blockState = iWorld.method_8320(blockPos);
			(blockState.isAir() || blockState.method_11602(BlockTags.field_15503)) && blockPos.getY() > 0;
			blockState = iWorld.method_8320(blockPos)
		) {
			blockPos = blockPos.down();
		}

		int i = 0;

		for (int j = 0; j < 128; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.method_8623(blockPos2) && grassFeatureConfig.state.method_11591(iWorld, blockPos2)) {
				iWorld.method_8652(blockPos2, grassFeatureConfig.state, 2);
				i++;
			}
		}

		return i > 0;
	}
}
