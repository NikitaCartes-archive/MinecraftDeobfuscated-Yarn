package net.minecraft;

import net.minecraft.world.biome.layer.BiomeLayers;

public enum class_3639 implements class_3664 {
	field_16059;

	@Override
	public int sample(class_3630 arg, int i) {
		if (BiomeLayers.isShallowOcean(i)) {
			return i;
		} else {
			int j = arg.nextInt(6);
			if (j == 0) {
				return 4;
			} else {
				return j == 1 ? 3 : 1;
			}
		}
	}
}
