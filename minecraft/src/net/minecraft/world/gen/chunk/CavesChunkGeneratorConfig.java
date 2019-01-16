package net.minecraft.world.gen.chunk;

public class CavesChunkGeneratorConfig extends ChunkGeneratorConfig {
	@Override
	public int getMinY() {
		return 0;
	}

	@Override
	public int getMaxY() {
		return 127;
	}
}
