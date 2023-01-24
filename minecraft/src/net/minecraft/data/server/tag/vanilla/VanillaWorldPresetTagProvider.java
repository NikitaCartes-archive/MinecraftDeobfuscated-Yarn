package net.minecraft.data.server.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.WorldPresetTags;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;

public class VanillaWorldPresetTagProvider extends TagProvider<WorldPreset> {
	public VanillaWorldPresetTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.WORLD_PRESET, registryLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(WorldPresetTags.NORMAL)
			.add(WorldPresets.DEFAULT)
			.add(WorldPresets.FLAT)
			.add(WorldPresets.LARGE_BIOMES)
			.add(WorldPresets.AMPLIFIED)
			.add(WorldPresets.SINGLE_BIOME_SURFACE);
		this.getOrCreateTagBuilder(WorldPresetTags.EXTENDED).addTag(WorldPresetTags.NORMAL).add(WorldPresets.DEBUG_ALL_BLOCK_STATES);
	}
}
