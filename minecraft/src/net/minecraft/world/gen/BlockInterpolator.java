package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public interface BlockInterpolator {
	BlockState sample(int x, int y, int z, ChunkGeneratorSettings settings);
}
