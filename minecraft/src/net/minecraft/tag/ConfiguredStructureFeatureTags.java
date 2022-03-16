package net.minecraft.tag;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

public interface ConfiguredStructureFeatureTags {
	TagKey<StructureFeature> EYE_OF_ENDER_LOCATED = of("eye_of_ender_located");
	TagKey<StructureFeature> DOLPHIN_LOCATED = of("dolphin_located");
	TagKey<StructureFeature> ON_WOODLAND_EXPLORER_MAPS = of("on_woodland_explorer_maps");
	TagKey<StructureFeature> ON_OCEAN_EXPLORER_MAPS = of("on_ocean_explorer_maps");
	TagKey<StructureFeature> ON_TREASURE_MAPS = of("on_treasure_maps");
	TagKey<StructureFeature> CATS_SPAWN_IN = of("cats_spawn_in");
	TagKey<StructureFeature> CATS_SPAWN_AS_BLACK = of("cats_spawn_as_black");
	TagKey<StructureFeature> VILLAGE = of("village");
	TagKey<StructureFeature> MINESHAFT = of("mineshaft");
	TagKey<StructureFeature> SHIPWRECK = of("shipwreck");
	TagKey<StructureFeature> RUINED_PORTAL = of("ruined_portal");
	TagKey<StructureFeature> OCEAN_RUIN = of("ocean_ruin");

	private static TagKey<StructureFeature> of(String id) {
		return TagKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(id));
	}
}
