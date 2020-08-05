package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddSunflowerPlainsLayer implements SouthEastSamplingLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int se) {
		return context.nextInt(57) == 0 && se == 1 ? 129 : se;
	}
}
