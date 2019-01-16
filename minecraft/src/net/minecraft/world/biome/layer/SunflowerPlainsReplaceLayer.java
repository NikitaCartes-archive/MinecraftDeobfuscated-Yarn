package net.minecraft.world.biome.layer;

import net.minecraft.class_3630;
import net.minecraft.class_3664;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum SunflowerPlainsReplaceLayer implements class_3664 {
	INSTANCE;

	private static final int PLAINS_ID = Registry.BIOME.getRawId(Biomes.field_9451);
	private static final int SUNFLOWER_PLAINS = Registry.BIOME.getRawId(Biomes.field_9455);

	@Override
	public int sample(class_3630 arg, int i) {
		return arg.nextInt(57) == 0 && i == PLAINS_ID ? SUNFLOWER_PLAINS : i;
	}
}
