package net.minecraft.world.gen.chunk;

import net.minecraft.class_5284;

public class OverworldChunkGeneratorConfig extends class_5284 {
	private final boolean field_24517;

	public OverworldChunkGeneratorConfig() {
		this(new ChunkGeneratorConfig(), false);
	}

	public OverworldChunkGeneratorConfig(ChunkGeneratorConfig chunkGeneratorConfig, boolean bl) {
		super(chunkGeneratorConfig);
		this.field_24517 = bl;
	}

	@Override
	public int getBedrockFloorY() {
		return 0;
	}

	public boolean method_28008() {
		return this.field_24517;
	}
}
