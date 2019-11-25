package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.BiomeLayers;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum AddRiversLayer implements MergingLayer, IdentityCoordinateTransformer {
	INSTANCE;

	private static final int FROZEN_RIVER_ID = Registry.BIOME.getRawId(Biomes.FROZEN_RIVER);
	private static final int SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.SNOWY_TUNDRA);
	private static final int MUSHROOM_FIELDS_ID = Registry.BIOME.getRawId(Biomes.MUSHROOM_FIELDS);
	private static final int MUSHROOM_FIELD_SHORE_ID = Registry.BIOME.getRawId(Biomes.MUSHROOM_FIELD_SHORE);
	private static final int RIVER_ID = Registry.BIOME.getRawId(Biomes.RIVER);

	@Override
	public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
		int i = sampler1.sample(this.transformX(x), this.transformZ(z));
		int j = sampler2.sample(this.transformX(x), this.transformZ(z));
		if (BiomeLayers.isOcean(i)) {
			return i;
		} else if (j == RIVER_ID) {
			if (i == SNOWY_TUNDRA_ID) {
				return FROZEN_RIVER_ID;
			} else {
				return i != MUSHROOM_FIELDS_ID && i != MUSHROOM_FIELD_SHORE_ID ? j & 0xFF : MUSHROOM_FIELD_SHORE_ID;
			}
		} else {
			return i;
		}
	}
}
