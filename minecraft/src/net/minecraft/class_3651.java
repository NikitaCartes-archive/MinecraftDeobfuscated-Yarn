package net.minecraft;

import net.minecraft.world.biome.layer.BiomeLayers;

public enum class_3651 implements class_3663 {
	field_16158;

	@Override
	public int sample(class_3630 arg, int i, int j, int k, int l, int m) {
		return BiomeLayers.isShallowOcean(m)
				&& BiomeLayers.isShallowOcean(i)
				&& BiomeLayers.isShallowOcean(j)
				&& BiomeLayers.isShallowOcean(l)
				&& BiomeLayers.isShallowOcean(k)
				&& arg.nextInt(2) == 0
			? 1
			: m;
	}
}
