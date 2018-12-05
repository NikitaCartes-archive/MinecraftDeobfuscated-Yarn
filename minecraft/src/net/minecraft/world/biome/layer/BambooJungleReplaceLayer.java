package net.minecraft.world.biome.layer;

import net.minecraft.class_3630;
import net.minecraft.class_3664;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public enum BambooJungleReplaceLayer implements class_3664 {
	INSTANCE;

	private static final int JUNGLE_ID = Registry.BIOME.getRawId(Biomes.field_9417);
	private static final int BAMBOO_JUNGLE_ID = Registry.BIOME.getRawId(Biomes.field_9440);

	@Override
	public int sample(class_3630 arg, int i) {
		return arg.nextInt(10) == 0 && i == JUNGLE_ID ? BAMBOO_JUNGLE_ID : i;
	}
}
