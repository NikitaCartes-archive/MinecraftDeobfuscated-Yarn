package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public interface StructureTags {
	TagKey<Structure> EYE_OF_ENDER_LOCATED = of("eye_of_ender_located");
	TagKey<Structure> DOLPHIN_LOCATED = of("dolphin_located");
	TagKey<Structure> ON_WOODLAND_EXPLORER_MAPS = of("on_woodland_explorer_maps");
	TagKey<Structure> ON_OCEAN_EXPLORER_MAPS = of("on_ocean_explorer_maps");
	TagKey<Structure> ON_TREASURE_MAPS = of("on_treasure_maps");
	TagKey<Structure> CATS_SPAWN_IN = of("cats_spawn_in");
	TagKey<Structure> CATS_SPAWN_AS_BLACK = of("cats_spawn_as_black");
	TagKey<Structure> VILLAGE = of("village");
	TagKey<Structure> MINESHAFT = of("mineshaft");
	TagKey<Structure> SHIPWRECK = of("shipwreck");
	TagKey<Structure> RUINED_PORTAL = of("ruined_portal");
	TagKey<Structure> OCEAN_RUIN = of("ocean_ruin");

	private static TagKey<Structure> of(String id) {
		return TagKey.of(RegistryKeys.STRUCTURE, new Identifier(id));
	}
}
