package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddBambooJungleLayer implements SouthEastSamplingLayer {
	field_16120;

	@Override
	public int sample(LayerRandomnessSource context, int se) {
		return context.nextInt(10) == 0 && se == 21 ? 168 : se;
	}
}
