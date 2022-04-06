package net.minecraft.tag;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.StructureType;

public interface ConfiguredStructureFeatureTags {
	TagKey<StructureType> EYE_OF_ENDER_LOCATED = of("eye_of_ender_located");
	TagKey<StructureType> DOLPHIN_LOCATED = of("dolphin_located");
	TagKey<StructureType> ON_WOODLAND_EXPLORER_MAPS = of("on_woodland_explorer_maps");
	TagKey<StructureType> ON_OCEAN_EXPLORER_MAPS = of("on_ocean_explorer_maps");
	TagKey<StructureType> ON_TREASURE_MAPS = of("on_treasure_maps");
	TagKey<StructureType> CATS_SPAWN_IN = of("cats_spawn_in");
	TagKey<StructureType> CATS_SPAWN_AS_BLACK = of("cats_spawn_as_black");
	TagKey<StructureType> VILLAGE = of("village");
	TagKey<StructureType> MINESHAFT = of("mineshaft");
	TagKey<StructureType> SHIPWRECK = of("shipwreck");
	TagKey<StructureType> RUINED_PORTAL = of("ruined_portal");
	TagKey<StructureType> OCEAN_RUIN = of("ocean_ruin");

	private static TagKey<StructureType> of(String id) {
		return TagKey.of(Registry.STRUCTURE_KEY, new Identifier(id));
	}
}
