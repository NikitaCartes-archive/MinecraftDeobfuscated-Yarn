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
	public GrassFeature(Function<Dynamic<?>, ? extends GrassFeatureConfig> configFactory) {
		super(configFactory);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, GrassFeatureConfig grassFeatureConfig
	) {
		for (BlockState blockState = iWorld.getBlockState(blockPos);
			(blockState.isAir() || blockState.matches(BlockTags.LEAVES)) && blockPos.getY() > 0;
			blockState = iWorld.getBlockState(blockPos)
		) {
			blockPos = blockPos.down();
		}

		int i = 0;

		for (int j = 0; j < 128; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.isAir(blockPos2) && grassFeatureConfig.state.canPlaceAt(iWorld, blockPos2)) {
				iWorld.setBlockState(blockPos2, grassFeatureConfig.state, 2);
				i++;
			}
		}

		return i > 0;
	}
}
