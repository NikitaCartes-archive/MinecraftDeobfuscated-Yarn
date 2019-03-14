package net.minecraft.world.gen.chunk;

import net.minecraft.util.math.BlockPos;

public class FloatingIslandsChunkGeneratorConfig extends ChunkGeneratorConfig {
	private BlockPos center;

	public FloatingIslandsChunkGeneratorConfig withCenter(BlockPos blockPos) {
		this.center = blockPos;
		return this;
	}

	public BlockPos getCenter() {
		return this.center;
	}
}
