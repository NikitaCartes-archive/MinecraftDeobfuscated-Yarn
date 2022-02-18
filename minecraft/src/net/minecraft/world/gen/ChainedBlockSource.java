package net.minecraft.world.gen;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.noise.NoiseType;

public record ChainedBlockSource(List<ChunkNoiseSampler.BlockStateSampler> samplers) implements ChunkNoiseSampler.BlockStateSampler {
	@Nullable
	@Override
	public BlockState sample(NoiseType.NoisePos pos) {
		for (ChunkNoiseSampler.BlockStateSampler blockStateSampler : this.samplers) {
			BlockState blockState = blockStateSampler.sample(pos);
			if (blockState != null) {
				return blockState;
			}
		}

		return null;
	}
}
