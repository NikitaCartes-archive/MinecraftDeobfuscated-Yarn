package net.minecraft.world.biome.layer;

import net.minecraft.class_3630;

public enum ContinentLayer implements InitLayer {
	field_16103;

	@Override
	public int sample(class_3630 arg, int i, int j) {
		if (i == 0 && j == 0) {
			return 1;
		} else {
			return arg.nextInt(10) == 0 ? 1 : BiomeLayers.OCEAN_ID;
		}
	}
}
