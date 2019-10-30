package net.minecraft.world.biome.layer;

import net.minecraft.util.math.noise.PerlinNoiseSampler;

public interface LayerRandomnessSource {
	int nextInt(int bound);

	PerlinNoiseSampler getNoiseSampler();
}
