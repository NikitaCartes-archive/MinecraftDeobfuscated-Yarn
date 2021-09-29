package net.minecraft.world.gen;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;

public class ChainedBlockSource implements BlockSource {
	private final List<BlockSource> samplers;

	public ChainedBlockSource(List<BlockSource> samplers) {
		this.samplers = samplers;
	}

	@Nullable
	@Override
	public BlockState apply(ChunkNoiseSampler chunkNoiseSampler, int i, int j, int k) {
		for (BlockSource blockSource : this.samplers) {
			BlockState blockState = blockSource.apply(chunkNoiseSampler, i, j, k);
			if (blockState != null) {
				return blockState;
			}
		}

		return null;
	}
}
