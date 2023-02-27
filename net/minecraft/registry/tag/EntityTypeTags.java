/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry.tag;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

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
    public static final TagKey<EntityType<?>> FROG_FOOD = EntityTypeTags.of("frog_food");
    public static final TagKey<EntityType<?>> FALL_DAMAGE_IMMUNE = EntityTypeTags.of("fall_damage_immune");
    public static final TagKey<EntityType<?>> DISMOUNTS_UNDERWATER = EntityTypeTags.of("dismounts_underwater");

    private EntityTypeTags() {
    }

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(id));
    }
}

