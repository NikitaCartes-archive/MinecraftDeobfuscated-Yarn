package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum ApplyRiverLayer implements MergingLayer, IdentityCoordinateTransformer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
		int i = sampler1.sample(this.transformX(x), this.transformZ(z));
		int j = sampler2.sample(this.transformX(x), this.transformZ(z));
		if (BiomeLayers.isOcean(i)) {
			return i;
		} else if (j == BiomeIds.RIVER) {
			if (i == BiomeIds.SNOWY_TUNDRA) {
				return BiomeIds.FROZEN_RIVER;
			} else {
				return i != BiomeIds.MUSHROOM_FIELDS && i != BiomeIds.MUSHROOM_FIELD_SHORE ? j & 0xFF : BiomeIds.MUSHROOM_FIELD_SHORE;
			}
		} else {
			return i;
		}
	}
}
