package net.minecraft.world.gen;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_6910;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;

public record ChainedBlockSource(List<ChunkNoiseSampler.BlockStateSampler> samplers) implements ChunkNoiseSampler.BlockStateSampler {
	@Nullable
	@Override
	public BlockState sample(class_6910.class_6912 arg) {
		for (ChunkNoiseSampler.BlockStateSampler blockStateSampler : this.samplers) {
			BlockState blockState = blockStateSampler.sample(arg);
			if (blockState != null) {
				return blockState;
			}
		}

		return null;
	}
}
