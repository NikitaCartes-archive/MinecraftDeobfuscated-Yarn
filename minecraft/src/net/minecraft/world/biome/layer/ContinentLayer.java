package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum ContinentLayer implements InitLayer {
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int x, int y) {
		if (x == 0 && y == 0) {
			return BiomeIds.PLAINS;
		} else {
			return context.nextInt(10) == 0 ? BiomeIds.PLAINS : BiomeIds.OCEAN;
		}
	}
}
