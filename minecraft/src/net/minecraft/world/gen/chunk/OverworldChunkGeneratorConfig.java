package net.minecraft.world.gen.chunk;

public class OverworldChunkGeneratorConfig extends ChunkGeneratorConfig {
	private final int field_13224 = 4;
	private final int field_13223 = 4;
	private final int field_13222 = -1;
	private final int field_13221 = 63;

	public int getBiomeSize() {
		return 4;
	}

	public int getRiverSize() {
		return 4;
	}

	public int getForcedBiome() {
		return -1;
	}

	@Override
	public int getMinY() {
		return 0;
	}
}
