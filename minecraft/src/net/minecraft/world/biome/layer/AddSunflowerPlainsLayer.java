package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddSunflowerPlainsLayer implements SouthEastSamplingLayer {
	INSTANCE;

	private static final int PLAINS_ID = BuiltinRegistries.BIOME.getRawId(Biomes.PLAINS);
	private static final int SUNFLOWER_PLAINS = BuiltinRegistries.BIOME.getRawId(Biomes.SUNFLOWER_PLAINS);

	@Override
	public int sample(LayerRandomnessSource context, int se) {
		return context.nextInt(57) == 0 && se == PLAINS_ID ? SUNFLOWER_PLAINS : se;
	}
}
