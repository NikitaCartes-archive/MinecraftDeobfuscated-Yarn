package net.minecraft.world.gen.chunk;

import net.minecraft.class_5284;

public class CavesChunkGeneratorConfig extends class_5284 {
	public CavesChunkGeneratorConfig(ChunkGeneratorConfig chunkGeneratorConfig) {
		super(chunkGeneratorConfig);
		chunkGeneratorConfig.field_24507 = 25;
		chunkGeneratorConfig.field_24508 = 10;
	}

	@Override
	public int getBedrockFloorY() {
		return 0;
	}

	@Override
	public int getBedrockCeilingY() {
		return 127;
	}
}
