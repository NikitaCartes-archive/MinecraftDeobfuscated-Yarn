package net.minecraft.registry.tag;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class EntityTypeTags {
	public static final TagKey<EntityType<?>> SKELETONS = of("skeletons");
	public static final TagKey<EntityType<?>> ZOMBIES = of("zombies");
	public static final TagKey<EntityType<?>> RAIDERS = of("raiders");
	public static final TagKey<EntityType<?>> UNDEAD = of("undead");
	public static final TagKey<EntityType<?>> BEEHIVE_INHABITORS = of("beehive_inhabitors");
	public static final TagKey<EntityType<?>> ARROWS = of("arrows");
	public static final TagKey<EntityType<?>> IMPACT_PROJECTILES = of("impact_projectiles");
	public static final TagKey<EntityType<?>> POWDER_SNOW_WALKABLE_MOBS = of("powder_snow_walkable_mobs");
	public static final TagKey<EntityType<?>> AXOLOTL_ALWAYS_HOSTILES = of("axolotl_always_hostiles");
	public static final TagKey<EntityType<?>> AXOLOTL_HUNT_TARGETS = of("axolotl_hunt_targets");
	public static final TagKey<EntityType<?>> FREEZE_IMMUNE_ENTITY_TYPES = of("freeze_immune_entity_types");
	public static final TagKey<EntityType<?>> FREEZE_HURTS_EXTRA_TYPES = of("freeze_hurts_extra_types");
	public static final TagKey<EntityType<?>> CAN_BREATHE_UNDER_WATER = of("can_breathe_under_water");
	public static final TagKey<EntityType<?>> FROG_FOOD = of("frog_food");
	public static final TagKey<EntityType<?>> FALL_DAMAGE_IMMUNE = of("fall_damage_immune");
	public static final TagKey<EntityType<?>> DISMOUNTS_UNDERWATER = of("dismounts_underwater");
	public static final TagKey<EntityType<?>> NON_CONTROLLING_RIDER = of("non_controlling_rider");
	public static final TagKey<EntityType<?>> DEFLECTS_ARROWS = of("deflects_arrows");
	public static final TagKey<EntityType<?>> DEFLECTS_TRIDENTS = of("deflects_tridents");

	private EntityTypeTags() {
	}

	private static TagKey<EntityType<?>> of(String id) {
		return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(id));
	}
}
