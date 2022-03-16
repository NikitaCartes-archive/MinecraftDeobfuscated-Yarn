package net.minecraft.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.tag.ConfiguredStructureFeatureTags;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatureKeys;
import net.minecraft.world.gen.feature.StructureFeature;

public class ConfiguredStructureFeatureTagProvider extends AbstractTagProvider<StructureFeature> {
	public ConfiguredStructureFeatureTagProvider(DataGenerator dataGenerator) {
		super(dataGenerator, BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.VILLAGE)
			.add(ConfiguredStructureFeatureKeys.VILLAGE_PLAINS)
			.add(ConfiguredStructureFeatureKeys.VILLAGE_DESERT)
			.add(ConfiguredStructureFeatureKeys.VILLAGE_SAVANNA)
			.add(ConfiguredStructureFeatureKeys.VILLAGE_SNOWY)
			.add(ConfiguredStructureFeatureKeys.VILLAGE_TAIGA);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.MINESHAFT)
			.add(ConfiguredStructureFeatureKeys.MINESHAFT)
			.add(ConfiguredStructureFeatureKeys.MINESHAFT_MESA);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.OCEAN_RUIN)
			.add(ConfiguredStructureFeatureKeys.OCEAN_RUIN_COLD)
			.add(ConfiguredStructureFeatureKeys.OCEAN_RUIN_WARM);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.SHIPWRECK)
			.add(ConfiguredStructureFeatureKeys.SHIPWRECK)
			.add(ConfiguredStructureFeatureKeys.SHIPWRECK_BEACHED);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.RUINED_PORTAL)
			.add(ConfiguredStructureFeatureKeys.RUINED_PORTAL_DESERT)
			.add(ConfiguredStructureFeatureKeys.RUINED_PORTAL_JUNGLE)
			.add(ConfiguredStructureFeatureKeys.RUINED_PORTAL_MOUNTAIN)
			.add(ConfiguredStructureFeatureKeys.RUINED_PORTAL_NETHER)
			.add(ConfiguredStructureFeatureKeys.RUINED_PORTAL_OCEAN)
			.add(ConfiguredStructureFeatureKeys.RUINED_PORTAL)
			.add(ConfiguredStructureFeatureKeys.RUINED_PORTAL_SWAMP);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.CATS_SPAWN_IN).add(ConfiguredStructureFeatureKeys.SWAMP_HUT);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.CATS_SPAWN_AS_BLACK).add(ConfiguredStructureFeatureKeys.SWAMP_HUT);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.EYE_OF_ENDER_LOCATED).add(ConfiguredStructureFeatureKeys.STRONGHOLD);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.DOLPHIN_LOCATED)
			.addTag(ConfiguredStructureFeatureTags.OCEAN_RUIN)
			.addTag(ConfiguredStructureFeatureTags.SHIPWRECK);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.ON_WOODLAND_EXPLORER_MAPS).add(ConfiguredStructureFeatureKeys.MANSION);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.ON_OCEAN_EXPLORER_MAPS).add(ConfiguredStructureFeatureKeys.MONUMENT);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.ON_TREASURE_MAPS).add(ConfiguredStructureFeatureKeys.BURIED_TREASURE);
	}
}
