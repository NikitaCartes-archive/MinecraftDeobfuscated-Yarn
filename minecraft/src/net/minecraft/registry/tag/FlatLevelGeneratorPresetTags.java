package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;

public class FlatLevelGeneratorPresetTags {
	public static final TagKey<FlatLevelGeneratorPreset> VISIBLE = of("visible");

	private FlatLevelGeneratorPresetTags() {
	}

	private static TagKey<FlatLevelGeneratorPreset> of(String id) {
		return TagKey.of(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, new Identifier(id));
	}
}
