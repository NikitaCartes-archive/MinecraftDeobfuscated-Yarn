package net.minecraft.world.level;

import net.minecraft.world.biome.Biome;

public interface ColorResolver {
	int getColor(Biome biome, double d, double e);
}
