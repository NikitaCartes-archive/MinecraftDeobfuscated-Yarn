package net.minecraft.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;

public class FlatLevelGeneratorPresetTagProvider extends AbstractTagProvider<FlatLevelGeneratorPreset> {
	public FlatLevelGeneratorPresetTagProvider(DataGenerator dataGenerator) {
		super(dataGenerator, BuiltinRegistries.FLAT_LEVEL_GENERATOR_PRESET);
	}

	@Override
	protected void configure() {
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
