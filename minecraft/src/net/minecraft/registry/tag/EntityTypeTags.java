package net.minecraft.registry.tag;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface EntityTypeTags {
	TagKey<EntityType<?>> SKELETONS = of("skeletons");
	TagKey<EntityType<?>> ZOMBIES = of("zombies");
	TagKey<EntityType<?>> RAIDERS = of("raiders");
	TagKey<EntityType<?>> UNDEAD = of("undead");
	TagKey<EntityType<?>> BEEHIVE_INHABITORS = of("beehive_inhabitors");
	TagKey<EntityType<?>> ARROWS = of("arrows");
	TagKey<EntityType<?>> IMPACT_PROJECTILES = of("impact_projectiles");
	TagKey<EntityType<?>> POWDER_SNOW_WALKABLE_MOBS = of("powder_snow_walkable_mobs");
	TagKey<EntityType<?>> AXOLOTL_ALWAYS_HOSTILES = of("axolotl_always_hostiles");
	TagKey<EntityType<?>> AXOLOTL_HUNT_TARGETS = of("axolotl_hunt_targets");
	TagKey<EntityType<?>> FREEZE_IMMUNE_ENTITY_TYPES = of("freeze_immune_entity_types");
	TagKey<EntityType<?>> FREEZE_HURTS_EXTRA_TYPES = of("freeze_hurts_extra_types");
	TagKey<EntityType<?>> CAN_BREATHE_UNDER_WATER = of("can_breathe_under_water");
	TagKey<EntityType<?>> FROG_FOOD = of("frog_food");
	TagKey<EntityType<?>> FALL_DAMAGE_IMMUNE = of("fall_damage_immune");
	TagKey<EntityType<?>> DISMOUNTS_UNDERWATER = of("dismounts_underwater");
	TagKey<EntityType<?>> NON_CONTROLLING_RIDER = of("non_controlling_rider");
	TagKey<EntityType<?>> DEFLECTS_PROJECTILES = of("deflects_projectiles");
	TagKey<EntityType<?>> CAN_TURN_IN_BOATS = of("can_turn_in_boats");
	TagKey<EntityType<?>> ILLAGER = of("illager");
	TagKey<EntityType<?>> AQUATIC = of("aquatic");
	TagKey<EntityType<?>> ARTHROPOD = of("arthropod");
	TagKey<EntityType<?>> IGNORES_POISON_AND_REGEN = of("ignores_poison_and_regen");
	TagKey<EntityType<?>> INVERTED_HEALING_AND_HARM = of("inverted_healing_and_harm");
	TagKey<EntityType<?>> WITHER_FRIENDS = of("wither_friends");
	TagKey<EntityType<?>> ILLAGER_FRIENDS = of("illager_friends");
	TagKey<EntityType<?>> NOT_SCARY_FOR_PUFFERFISH = of("not_scary_for_pufferfish");
	TagKey<EntityType<?>> SENSITIVE_TO_IMPALING = of("sensitive_to_impaling");
	TagKey<EntityType<?>> SENSITIVE_TO_BANE_OF_ARTHROPODS = of("sensitive_to_bane_of_arthropods");
	TagKey<EntityType<?>> SENSITIVE_TO_SMITE = of("sensitive_to_smite");
	TagKey<EntityType<?>> NO_ANGER_FROM_WIND_CHARGE = of("no_anger_from_wind_charge");
	TagKey<EntityType<?>> IMMUNE_TO_OOZING = of("immune_to_oozing");
	TagKey<EntityType<?>> IMMUNE_TO_INFESTED = of("immune_to_infested");
	TagKey<EntityType<?>> REDIRECTABLE_PROJECTILE = of("redirectable_projectile");

	private static TagKey<EntityType<?>> of(String id) {
		return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.ofVanilla(id));
	}
}
