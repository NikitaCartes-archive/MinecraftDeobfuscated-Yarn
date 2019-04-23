package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum AddSunflowerPlainsLayer implements SouthEastSamplingLayer {
	field_16155;

	private static final int PLAINS_ID = Registry.BIOME.getRawId(Biomes.field_9451);
	private static final int SUNFLOWER_PLAINS = Registry.BIOME.getRawId(Biomes.field_9455);

	@Override
	public int sample(LayerRandomnessSource layerRandomnessSource, int i) {
		return layerRandomnessSource.nextInt(57) == 0 && i == PLAINS_ID ? SUNFLOWER_PLAINS : i;
	}
}
