package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public abstract class FlowerFeature<U extends FeatureConfig> extends Feature<U> {
	public FlowerFeature(Function<Dynamic<?>, ? extends U> function) {
		super(function);
	}

	@Override
	public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, U featureConfig) {
		BlockState blockState = this.getFlowerToPlace(random, blockPos, featureConfig);
		int i = 0;

		for (int j = 0; j < this.method_23370(featureConfig); j++) {
			BlockPos blockPos2 = this.method_23371(random, blockPos, featureConfig);
			if (iWorld.isAir(blockPos2) && blockPos2.getY() < 255 && blockState.canPlaceAt(iWorld, blockPos2) && this.method_23369(iWorld, blockPos2, featureConfig)) {
				iWorld.setBlockState(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}

	public abstract boolean method_23369(IWorld iWorld, BlockPos blockPos, U featureConfig);

	public abstract int method_23370(U featureConfig);

	public abstract BlockPos method_23371(Random random, BlockPos blockPos, U featureConfig);

	public abstract BlockState getFlowerToPlace(Random random, BlockPos blockPos, U featureConfig);
}
