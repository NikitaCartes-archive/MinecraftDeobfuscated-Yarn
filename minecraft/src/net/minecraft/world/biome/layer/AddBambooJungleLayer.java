package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddBambooJungleLayer implements SouthEastSamplingLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int se) {
		return context.nextInt(10) == 0 && se == BiomeIds.JUNGLE ? BiomeIds.BAMBOO_JUNGLE : se;
	}
}
