package net.minecraft.world.level;

import net.minecraft.world.biome.Biome;

@FunctionalInterface
public interface ColorResolver {
	int getColor(Biome biome, double x, double z);
}
