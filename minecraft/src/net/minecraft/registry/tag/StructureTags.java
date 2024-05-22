package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public interface StructureTags {
	TagKey<Structure> EYE_OF_ENDER_LOCATED = of("eye_of_ender_located");
	TagKey<Structure> DOLPHIN_LOCATED = of("dolphin_located");
	TagKey<Structure> ON_WOODLAND_EXPLORER_MAPS = of("on_woodland_explorer_maps");
	TagKey<Structure> ON_OCEAN_EXPLORER_MAPS = of("on_ocean_explorer_maps");
	TagKey<Structure> ON_SAVANNA_VILLAGE_MAPS = of("on_savanna_village_maps");
	TagKey<Structure> ON_DESERT_VILLAGE_MAPS = of("on_desert_village_maps");
	TagKey<Structure> ON_PLAINS_VILLAGE_MAPS = of("on_plains_village_maps");
	TagKey<Structure> ON_TAIGA_VILLAGE_MAPS = of("on_taiga_village_maps");
	TagKey<Structure> ON_SNOWY_VILLAGE_MAPS = of("on_snowy_village_maps");
	TagKey<Structure> ON_JUNGLE_EXPLORER_MAPS = of("on_jungle_explorer_maps");
	TagKey<Structure> ON_SWAMP_EXPLORER_MAPS = of("on_swamp_explorer_maps");
	TagKey<Structure> ON_TREASURE_MAPS = of("on_treasure_maps");
	TagKey<Structure> ON_TRIAL_CHAMBERS_MAPS = of("on_trial_chambers_maps");
	TagKey<Structure> CATS_SPAWN_IN = of("cats_spawn_in");
	TagKey<Structure> CATS_SPAWN_AS_BLACK = of("cats_spawn_as_black");
	TagKey<Structure> VILLAGE = of("village");
	TagKey<Structure> MINESHAFT = of("mineshaft");
	TagKey<Structure> SHIPWRECK = of("shipwreck");
	TagKey<Structure> RUINED_PORTAL = of("ruined_portal");
	TagKey<Structure> OCEAN_RUIN = of("ocean_ruin");

	private static TagKey<Structure> of(String id) {
		return TagKey.of(RegistryKeys.STRUCTURE, Identifier.ofVanilla(id));
	}
}
