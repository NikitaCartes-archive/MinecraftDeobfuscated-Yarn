package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.DiagonalCrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddMushroomIslandLayer implements DiagonalCrossSamplingLayer {
	INSTANCE;

	private static final int MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);

	@Override
	public int sample(LayerRandomnessSource context, int sw, int se, int ne, int nw, int center) {
		return BiomeLayers.isShallowOcean(center)
				&& BiomeLayers.isShallowOcean(nw)
				&& BiomeLayers.isShallowOcean(sw)
				&& BiomeLayers.isShallowOcean(ne)
				&& BiomeLayers.isShallowOcean(se)
				&& context.nextInt(100) == 0
			? MUSHROOM_FIELDS_ID
			: center;
	}
}
