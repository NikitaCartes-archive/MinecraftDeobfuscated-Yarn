package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum AddMushroomIslandLayer implements DiagonalCrossSamplingLayer {
	INSTANCE;

	private static final int MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
		return BiomeLayers.isShallowOcean(m)
				&& BiomeLayers.isShallowOcean(l)
				&& BiomeLayers.isShallowOcean(i)
				&& BiomeLayers.isShallowOcean(k)
				&& BiomeLayers.isShallowOcean(j)
				&& layerRandomnessSource.nextInt(100) == 0
			? MUSHROOM_FIELDS_ID
			: m;
	}
}
