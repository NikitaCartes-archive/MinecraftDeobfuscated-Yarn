package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum AddRiversLayer implements MergingLayer, IdentityCoordinateTransformer {
	field_16161;

	private static final int FROZEN_RIVER_ID = Registry.BIOME.getRawId(Biomes.field_9463);
	private static final int SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.field_9452);
	private static final int MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.field_9462);
	private static final int MUSHROOM_FIELD_SHORE_ID = Registry.BIOME.getRawId(Biomes.field_9407);
	private static final int RIVER_ID = Registry.BIOME.getRawId(Biomes.field_9438);

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, LayerSampler layerSampler, LayerSampler layerSampler2, int i, int j) {
		int k = layerSampler.sample(this.transformX(i), this.transformZ(j));
		int l = layerSampler2.sample(this.transformX(i), this.transformZ(j));
		if (BiomeLayers.isOcean(k)) {
			return k;
		} else if (l == RIVER_ID) {
			if (k == SNOWY_TUNDRA_ID) {
				return FROZEN_RIVER_ID;
			} else {
				return k != MUSHROOM_FIELDS_ID && k != MUSHROOM_FIELD_SHORE_ID ? l & 0xFF : MUSHROOM_FIELD_SHORE_ID;
			}
		} else {
			return k;
		}
	}
}
