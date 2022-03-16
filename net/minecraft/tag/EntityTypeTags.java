/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class EntityTypeTags {
    public static final TagKey<EntityType<?>> SKELETONS = EntityTypeTags.of("skeletons");
    public static final TagKey<EntityType<?>> RAIDERS = EntityTypeTags.of("raiders");
    public static final TagKey<EntityType<?>> BEEHIVE_INHABITORS = EntityTypeTags.of("beehive_inhabitors");
    public static final TagKey<EntityType<?>> ARROWS = EntityTypeTags.of("arrows");
    public static final TagKey<EntityType<?>> IMPACT_PROJECTILES = EntityTypeTags.of("impact_projectiles");
    public static final TagKey<EntityType<?>> POWDER_SNOW_WALKABLE_MOBS = EntityTypeTags.of("powder_snow_walkable_mobs");
    public static final TagKey<EntityType<?>> AXOLOTL_ALWAYS_HOSTILES = EntityTypeTags.of("axolotl_always_hostiles");
    public static final TagKey<EntityType<?>> AXOLOTL_HUNT_TARGETS = EntityTypeTags.of("axolotl_hunt_targets");
    public static final TagKey<EntityType<?>> FREEZE_IMMUNE_ENTITY_TYPES = EntityTypeTags.of("freeze_immune_entity_types");
    public static final TagKey<EntityType<?>> FREEZE_HURTS_EXTRA_TYPES = EntityTypeTags.of("freeze_hurts_extra_types");

    private EntityTypeTags() {
    }

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier(id));
    }
}

