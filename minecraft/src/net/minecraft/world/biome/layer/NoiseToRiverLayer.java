package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum NoiseToRiverLayer implements CrossSamplingLayer {
	field_16168;

	public static final int RIVER_ID = Registry.BIOME.getRawId(Biomes.field_9438);

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
		int n = isValidForRiver(m);
		return n == isValidForRiver(l) && n == isValidForRiver(i) && n == isValidForRiver(j) && n == isValidForRiver(k) ? -1 : RIVER_ID;
	}

	private static int isValidForRiver(int i) {
		return i >= 2 ? 2 + (i & 1) : i;
	}
}
