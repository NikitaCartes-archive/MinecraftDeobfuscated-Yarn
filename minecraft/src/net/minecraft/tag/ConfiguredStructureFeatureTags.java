package net.minecraft.tag;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public interface ConfiguredStructureFeatureTags {
	TagKey<ConfiguredStructureFeature<?, ?>> EYE_OF_ENDER_LOCATED = of("eye_of_ender_located");
	TagKey<ConfiguredStructureFeature<?, ?>> DOLPHIN_LOCATED = of("dolphin_located");
	TagKey<ConfiguredStructureFeature<?, ?>> ON_WOODLAND_EXPLORER_MAPS = of("on_woodland_explorer_maps");
	TagKey<ConfiguredStructureFeature<?, ?>> ON_OCEAN_EXPLORER_MAPS = of("on_ocean_explorer_maps");
	TagKey<ConfiguredStructureFeature<?, ?>> ON_TREASURE_MAPS = of("on_treasure_maps");
	TagKey<ConfiguredStructureFeature<?, ?>> VILLAGE = of("village");
	TagKey<ConfiguredStructureFeature<?, ?>> MINESHAFT = of("mineshaft");
	TagKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK = of("shipwreck");
	TagKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL = of("ruined_portal");
	TagKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN = of("ocean_ruin");

	private static TagKey<ConfiguredStructureFeature<?, ?>> of(String id) {
		return TagKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(id));
	}
}
