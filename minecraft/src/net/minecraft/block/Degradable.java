package net.minecraft.block;

import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public interface Degradable<T extends Enum<T>> {
	int DEGRADING_RANGE = 4;

	Optional<BlockState> getDegradationResult(BlockState state);

	float getDegradationChanceMultiplier();

	default void tickDegradation(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		float f = 0.05688889F;
		if (random.nextFloat() < 0.05688889F) {
			this.tryDegrade(state, world, pos, random).ifPresent(degraded -> world.setBlockState(pos, degraded));
		}
	}

	T getDegradationLevel();

	default Optional<BlockState> tryDegrade(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		int i = this.getDegradationLevel().ordinal();
		int j = 0;
		int k = 0;

		for (BlockPos blockPos : BlockPos.iterateOutwards(pos, 4, 4, 4)) {
			int l = blockPos.getManhattanDistance(pos);
			if (l > 4) {
				break;
			}

			if (!blockPos.equals(pos) && world.getBlockState(blockPos).getBlock() instanceof Degradable<?> degradable) {
				Enum<?> enum_ = degradable.getDegradationLevel();
				if (this.getDegradationLevel().getClass() == enum_.getClass()) {
					int m = enum_.ordinal();
					if (m < i) {
						return Optional.empty();
					}

					if (m > i) {
						k++;
					} else {
						j++;
					}
				}
			}
		}

		float f = (float)(k + 1) / (float)(k + j + 1);
		float g = f * f * this.getDegradationChanceMultiplier();
		return random.nextFloat() < g ? this.getDegradationResult(state) : Optional.empty();
	}
}
