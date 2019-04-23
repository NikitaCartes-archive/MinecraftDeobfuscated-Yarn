package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum AddBambooJungleLayer implements SouthEastSamplingLayer {
	field_16120;

	private static final int JUNGLE_ID = Registry.BIOME.getRawId(Biomes.field_9417);
	private static final int BAMBOO_JUNGLE_ID = Registry.BIOME.getRawId(Biomes.field_9440);

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i) {
		return layerRandomnessSource.nextInt(10) == 0 && i == JUNGLE_ID ? BAMBOO_JUNGLE_ID : i;
	}
}
