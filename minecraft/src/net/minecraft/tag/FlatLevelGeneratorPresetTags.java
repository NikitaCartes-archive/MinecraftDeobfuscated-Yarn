package net.minecraft.tag;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;

public class FlatLevelGeneratorPresetTags {
	public static final TagKey<FlatLevelGeneratorPreset> VISIBLE = of("visible");

	private FlatLevelGeneratorPresetTags() {
	}

	private static TagKey<FlatLevelGeneratorPreset> of(String id) {
		return TagKey.of(Registry.FLAT_LEVEL_GENERATOR_PRESET_WORLDGEN, new Identifier(id));
	}
}
