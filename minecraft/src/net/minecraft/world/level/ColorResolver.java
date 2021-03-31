package net.minecraft.world.level;

import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.world.biome.Biome;

public interface ColorResolver {
	@DontObfuscate
	int getColor(Biome biome, double x, double z);
}
