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
	public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos pos, U config) {
		BlockState blockState = this.getFlowerState(random, pos, config);
		int i = 0;

		for (int j = 0; j < this.getFlowerAmount(config); j++) {
			BlockPos blockPos = this.getPos(random, pos, config);
			if (world.isAir(blockPos) && blockPos.getY() < 255 && blockState.canPlaceAt(world, blockPos) && this.isPosValid(world, blockPos, config)) {
				world.setBlockState(blockPos, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}

	public abstract boolean isPosValid(IWorld world, BlockPos pos, U config);

	public abstract int getFlowerAmount(U config);

	public abstract BlockPos getPos(Random random, BlockPos pos, U config);

	public abstract BlockState getFlowerState(Random random, BlockPos pos, U config);
}
