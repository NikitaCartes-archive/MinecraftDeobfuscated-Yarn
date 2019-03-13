package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public abstract class FlowerFeature extends Feature<DefaultFeatureConfig> {
	public FlowerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function, false);
	}

	public boolean method_13176(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockState blockState = this.method_13175(random, blockPos);
		int i = 0;

		for (int j = 0; j < 64; j++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (iWorld.method_8623(blockPos2) && blockPos2.getY() < 255 && blockState.method_11591(iWorld, blockPos2)) {
				iWorld.method_8652(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}

	public abstract BlockState method_13175(Random random, BlockPos blockPos);
}
