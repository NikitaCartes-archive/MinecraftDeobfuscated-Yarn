package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class BadlandsBiome extends Biome {
	public BadlandsBiome(Biome.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getFoliageColor() {
		return 10387789;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getGrassColorAt(double x, double z) {
		return 9470285;
	}
}
