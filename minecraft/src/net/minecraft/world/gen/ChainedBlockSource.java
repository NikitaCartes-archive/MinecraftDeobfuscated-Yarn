package net.minecraft.world.gen;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record ChainedBlockSource(List<ChunkNoiseSampler.BlockStateSampler> samplers) implements ChunkNoiseSampler.BlockStateSampler {
	@Nullable
	@Override
	public BlockState sample(DensityFunction.NoisePos pos) {
		for (ChunkNoiseSampler.BlockStateSampler blockStateSampler : this.samplers) {
			BlockState blockState = blockStateSampler.sample(pos);
			if (blockState != null) {
				return blockState;
			}
		}

		return null;
	}
}
