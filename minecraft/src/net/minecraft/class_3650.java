package net.minecraft;

import net.minecraft.world.biome.layer.BiomeLayers;

public enum class_3650 implements class_3661 {
	field_16157;

	@Override
	public int method_15866(class_3630 arg, int i) {
		return BiomeLayers.isShallowOcean(i) ? i : arg.nextInt(299999) + 2;
	}
}
