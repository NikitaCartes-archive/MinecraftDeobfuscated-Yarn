package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;

public class FlatLevelGeneratorPresetTagProvider extends AbstractTagProvider<FlatLevelGeneratorPreset> {
	public FlatLevelGeneratorPresetTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, registryLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
		this.getOrCreateTagBuilder(FlatLevelGeneratorPresetTags.VISIBLE)
			.add(FlatLevelGeneratorPresets.CLASSIC_FLAT)
			.add(FlatLevelGeneratorPresets.TUNNELERS_DREAM)
			.add(FlatLevelGeneratorPresets.WATER_WORLD)
			.add(FlatLevelGeneratorPresets.OVERWORLD)
			.add(FlatLevelGeneratorPresets.SNOWY_KINGDOM)
			.add(FlatLevelGeneratorPresets.BOTTOMLESS_PIT)
			.add(FlatLevelGeneratorPresets.DESERT)
			.add(FlatLevelGeneratorPresets.REDSTONE_READY)
			.add(FlatLevelGeneratorPresets.THE_VOID);
	}
}
