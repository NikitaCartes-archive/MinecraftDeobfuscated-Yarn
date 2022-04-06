package net.minecraft.data.server;

import net.minecraft.data.DataGenerator;
import net.minecraft.tag.ConfiguredStructureFeatureTags;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.gen.structure.StructureTypeKeys;

public class ConfiguredStructureFeatureTagProvider extends AbstractTagProvider<StructureType> {
	public ConfiguredStructureFeatureTagProvider(DataGenerator dataGenerator) {
		super(dataGenerator, BuiltinRegistries.STRUCTURE);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.VILLAGE)
			.add(StructureTypeKeys.VILLAGE_PLAINS)
			.add(StructureTypeKeys.VILLAGE_DESERT)
			.add(StructureTypeKeys.VILLAGE_SAVANNA)
			.add(StructureTypeKeys.VILLAGE_SNOWY)
			.add(StructureTypeKeys.VILLAGE_TAIGA);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.MINESHAFT).add(StructureTypeKeys.MINESHAFT).add(StructureTypeKeys.MINESHAFT_MESA);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.OCEAN_RUIN).add(StructureTypeKeys.OCEAN_RUIN_COLD).add(StructureTypeKeys.OCEAN_RUIN_WARM);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.SHIPWRECK).add(StructureTypeKeys.SHIPWRECK).add(StructureTypeKeys.SHIPWRECK_BEACHED);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.RUINED_PORTAL)
			.add(StructureTypeKeys.RUINED_PORTAL_DESERT)
			.add(StructureTypeKeys.RUINED_PORTAL_JUNGLE)
			.add(StructureTypeKeys.RUINED_PORTAL_MOUNTAIN)
			.add(StructureTypeKeys.RUINED_PORTAL_NETHER)
			.add(StructureTypeKeys.RUINED_PORTAL_OCEAN)
			.add(StructureTypeKeys.RUINED_PORTAL)
			.add(StructureTypeKeys.RUINED_PORTAL_SWAMP);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.CATS_SPAWN_IN).add(StructureTypeKeys.SWAMP_HUT);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.CATS_SPAWN_AS_BLACK).add(StructureTypeKeys.SWAMP_HUT);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.EYE_OF_ENDER_LOCATED).add(StructureTypeKeys.STRONGHOLD);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.DOLPHIN_LOCATED)
			.addTag(ConfiguredStructureFeatureTags.OCEAN_RUIN)
			.addTag(ConfiguredStructureFeatureTags.SHIPWRECK);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.ON_WOODLAND_EXPLORER_MAPS).add(StructureTypeKeys.MANSION);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.ON_OCEAN_EXPLORER_MAPS).add(StructureTypeKeys.MONUMENT);
		this.getOrCreateTagBuilder(ConfiguredStructureFeatureTags.ON_TREASURE_MAPS).add(StructureTypeKeys.BURIED_TREASURE);
	}
}
