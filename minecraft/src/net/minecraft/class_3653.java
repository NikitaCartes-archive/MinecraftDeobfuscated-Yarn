package net.minecraft;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum class_3653 implements class_3663 {
	INSTANCE;

	public static final int RIVER_ID = Registry.BIOME.getRawId(Biomes.field_9438);

	@Override
	public int sample(class_3630 arg, int i, int j, int k, int l, int m) {
		int n = method_15850(m);
		return n == method_15850(l) && n == method_15850(i) && n == method_15850(j) && n == method_15850(k) ? -1 : RIVER_ID;
	}

	private static int method_15850(int i) {
		return i >= 2 ? 2 + (i & 1) : i;
	}
}
