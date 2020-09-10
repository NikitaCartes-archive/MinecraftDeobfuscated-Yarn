package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class FlowerFeature<U extends FeatureConfig> extends Feature<U> {
	public FlowerFeature(Codec<U> codec) {
		super(codec);
	}

	@Override
	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, U config) {
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

	public abstract boolean isPosValid(WorldAccess world, BlockPos pos, U config);

	public abstract int getFlowerAmount(U config);

	public abstract BlockPos getPos(Random random, BlockPos pos, U config);

	public abstract BlockState getFlowerState(Random random, BlockPos pos, U config);
}
