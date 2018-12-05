package net.minecraft.world.gen.chunk;

import net.minecraft.util.math.BlockPos;

public class FloatingIslandsChunkGeneratorSettings extends ChunkGeneratorSettings {
	private BlockPos field_13272;

	public FloatingIslandsChunkGeneratorSettings method_12651(BlockPos blockPos) {
		this.field_13272 = blockPos;
		return this;
	}

	public BlockPos method_12652() {
		return this.field_13272;
	}
}
