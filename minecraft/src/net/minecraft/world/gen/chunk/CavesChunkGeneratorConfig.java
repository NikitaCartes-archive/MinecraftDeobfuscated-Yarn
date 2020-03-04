package net.minecraft.world.gen.chunk;

public class CavesChunkGeneratorConfig extends ChunkGeneratorConfig {
	@Override
	public int getBedrockFloorY() {
		return 0;
	}

	@Override
	public int getBedrockCeilingY() {
		return 127;
	}
}
