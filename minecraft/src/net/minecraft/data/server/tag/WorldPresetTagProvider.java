package net.minecraft.data.server.tag;

import net.minecraft.data.DataOutput;
import net.minecraft.tag.WorldPresetTags;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;

public class WorldPresetTagProvider extends AbstractTagProvider<WorldPreset> {
	public WorldPresetTagProvider(DataOutput dataGenerator) {
		super(dataGenerator, BuiltinRegistries.WORLD_PRESET);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(WorldPresetTags.NORMAL)
			.add(WorldPresets.DEFAULT)
			.add(WorldPresets.FLAT)
			.add(WorldPresets.LARGE_BIOMES)
			.add(WorldPresets.AMPLIFIED)
			.add(WorldPresets.SINGLE_BIOME_SURFACE);
		this.getOrCreateTagBuilder(WorldPresetTags.EXTENDED).addTag(WorldPresetTags.NORMAL).add(WorldPresets.DEBUG_ALL_BLOCK_STATES);
	}
}
