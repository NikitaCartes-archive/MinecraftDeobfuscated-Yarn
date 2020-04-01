package net.minecraft.world.gen.chunk;

import java.util.Random;
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

	public FloatingIslandsChunkGeneratorConfig() {
	}

	public FloatingIslandsChunkGeneratorConfig(Random random) {
		this.defaultBlock = this.method_26576(random);
		this.defaultFluid = this.method_26575(random);
	}
}
