package net.minecraft;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;

public enum class_3637 implements class_3662 {
	INSTANCE;

	private static final int MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.field_9462);

	@Override
	public int method_15867(class_3630 arg, int i, int j, int k, int l, int m) {
		return BiomeLayers.isShallowOcean(m)
				&& BiomeLayers.isShallowOcean(l)
				&& BiomeLayers.isShallowOcean(i)
				&& BiomeLayers.isShallowOcean(k)
				&& BiomeLayers.isShallowOcean(j)
				&& arg.nextInt(100) == 0
			? MUSHROOM_FIELDS_ID
			: m;
	}
}
