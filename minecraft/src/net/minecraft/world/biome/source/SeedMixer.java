package net.minecraft.world.biome.source;

public class SeedMixer {
	private static final long field_29842 = 6364136223846793005L;
	private static final long field_29843 = 1442695040888963407L;

	public static long mixSeed(long seed, long salt) {
		seed *= seed * 6364136223846793005L + 1442695040888963407L;
		return seed + salt;
	}
}
