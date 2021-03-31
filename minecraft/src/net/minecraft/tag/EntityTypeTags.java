package net.minecraft.tag;

import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

public final class EntityTypeTags {
	protected static final RequiredTagList<EntityType<?>> REQUIRED_TAGS = RequiredTagListRegistry.register(Registry.ENTITY_TYPE_KEY, "tags/entity_types");
	public static final Tag.Identified<EntityType<?>> SKELETONS = register("skeletons");
	public static final Tag.Identified<EntityType<?>> RAIDERS = register("raiders");
	public static final Tag.Identified<EntityType<?>> BEEHIVE_INHABITORS = register("beehive_inhabitors");
	public static final Tag.Identified<EntityType<?>> ARROWS = register("arrows");
	public static final Tag.Identified<EntityType<?>> IMPACT_PROJECTILES = register("impact_projectiles");
	public static final Tag.Identified<EntityType<?>> POWDER_SNOW_WALKABLE_MOBS = register("powder_snow_walkable_mobs");
	public static final Tag.Identified<EntityType<?>> AXOLOTL_ALWAYS_HOSTILES = register("axolotl_always_hostiles");
	public static final Tag.Identified<EntityType<?>> AXOLOTL_HUNT_TARGETS = register("axolotl_hunt_targets");
	public static final Tag.Identified<EntityType<?>> FREEZE_IMMUNE_ENTITY_TYPES = register("freeze_immune_entity_types");
	public static final Tag.Identified<EntityType<?>> FREEZE_HURTS_EXTRA_TYPES = register("freeze_hurts_extra_types");

	private EntityTypeTags() {
	}

	private static Tag.Identified<EntityType<?>> register(String id) {
		return REQUIRED_TAGS.add(id);
	}

	public static TagGroup<EntityType<?>> getTagGroup() {
		return REQUIRED_TAGS.getGroup();
	}
}
