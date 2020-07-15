package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class DarkForestBiome extends Biome {
	public DarkForestBiome(Biome.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getGrassColorAt(double x, double z) {
		int i = super.getGrassColorAt(x, z);
		return (i & 16711422) + 2634762 >> 1;
	}
}
