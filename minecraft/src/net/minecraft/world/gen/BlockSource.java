package net.minecraft.world.gen;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;

public interface BlockSource {
	@Nullable
	BlockState apply(ChunkNoiseSampler sampler, int x, int y, int z);
}
