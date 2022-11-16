package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.WorldPreset;

public class WorldPresetTags {
	public static final TagKey<WorldPreset> NORMAL = of("normal");
	public static final TagKey<WorldPreset> EXTENDED = of("extended");

	private WorldPresetTags() {
	}

	private static TagKey<WorldPreset> of(String id) {
		return TagKey.of(RegistryKeys.WORLD_PRESET, new Identifier(id));
	}
}
