package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.SouthEastSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum AddBambooJungleLayer implements SouthEastSamplingLayer {
	INSTANCE;

	private static final int JUNGLE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.JUNGLE);
	private static final int BAMBOO_JUNGLE_ID = BuiltinRegistries.BIOME.getRawId(Biomes.BAMBOO_JUNGLE);

	@Override
	public int sample(LayerRandomnessSource context, int se) {
		return context.nextInt(10) == 0 && se == JUNGLE_ID ? BAMBOO_JUNGLE_ID : se;
	}
}
