/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class EntityTypeTags {
    public static final TagKey<EntityType<?>> SKELETONS = EntityTypeTags.register("skeletons");
    public static final TagKey<EntityType<?>> RAIDERS = EntityTypeTags.register("raiders");
    public static final TagKey<EntityType<?>> BEEHIVE_INHABITORS = EntityTypeTags.register("beehive_inhabitors");
    public static final TagKey<EntityType<?>> ARROWS = EntityTypeTags.register("arrows");
    public static final TagKey<EntityType<?>> IMPACT_PROJECTILES = EntityTypeTags.register("impact_projectiles");
    public static final TagKey<EntityType<?>> POWDER_SNOW_WALKABLE_MOBS = EntityTypeTags.register("powder_snow_walkable_mobs");
    public static final TagKey<EntityType<?>> AXOLOTL_ALWAYS_HOSTILES = EntityTypeTags.register("axolotl_always_hostiles");
    public static final TagKey<EntityType<?>> AXOLOTL_HUNT_TARGETS = EntityTypeTags.register("axolotl_hunt_targets");
    public static final TagKey<EntityType<?>> FREEZE_IMMUNE_ENTITY_TYPES = EntityTypeTags.register("freeze_immune_entity_types");
    public static final TagKey<EntityType<?>> FREEZE_HURTS_EXTRA_TYPES = EntityTypeTags.register("freeze_hurts_extra_types");

    private EntityTypeTags() {
    }

    private static TagKey<EntityType<?>> register(String id) {
        return TagKey.intern(Registry.ENTITY_TYPE_KEY, new Identifier(id));
    }
}

