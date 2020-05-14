package net.minecraft.world.gen.chunk;

import net.minecraft.class_5284;

public class OverworldChunkGeneratorConfig extends class_5284 {
	private final boolean old;

	public OverworldChunkGeneratorConfig() {
		this(new ChunkGeneratorConfig(), false);
	}

	public OverworldChunkGeneratorConfig(ChunkGeneratorConfig config, boolean old) {
		super(config);
		this.old = old;
	}

	@Override
	public int getBedrockFloorY() {
		return 0;
	}

	public boolean isOld() {
		return this.old;
	}
}
