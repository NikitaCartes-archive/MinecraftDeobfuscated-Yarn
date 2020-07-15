package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class SwampBiome extends Biome {
	public SwampBiome(Biome.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getGrassColorAt(double x, double z) {
		double d = FOLIAGE_NOISE.sample(x * 0.0225, z * 0.0225, false);
		return d < -0.1 ? 5011004 : 6975545;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getFoliageColor() {
		return 6975545;
	}
}
