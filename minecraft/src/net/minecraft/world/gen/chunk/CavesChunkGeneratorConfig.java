package net.minecraft.world.gen.chunk;

import java.util.Random;

public class CavesChunkGeneratorConfig extends ChunkGeneratorConfig {
	@Override
	public int getBedrockFloorY() {
		return 0;
	}

	@Override
	public int getBedrockCeilingY() {
		return 127;
	}

	public CavesChunkGeneratorConfig() {
	}

	public CavesChunkGeneratorConfig(Random random) {
		this.defaultBlock = this.method_26576(random);
		this.defaultFluid = this.method_26575(random);
	}
}
