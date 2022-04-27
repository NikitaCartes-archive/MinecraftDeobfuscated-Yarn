package net.minecraft.tag;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.WorldPreset;

public class WorldPresetTags {
	public static final TagKey<WorldPreset> NORMAL = of("normal");
	public static final TagKey<WorldPreset> EXTENDED = of("extended");

	private WorldPresetTags() {
	}

	private static TagKey<WorldPreset> of(String id) {
		return TagKey.of(Registry.WORLD_PRESET_KEY, new Identifier(id));
	}
}
