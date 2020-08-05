/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;

public final class EntityTypeTags {
    protected static final RequiredTagList<EntityType<?>> REQUIRED_TAGS = RequiredTagListRegistry.register(new Identifier("entity_type"), TagManager::getEntityTypes);
    public static final Tag.Identified<EntityType<?>> SKELETONS = EntityTypeTags.register("skeletons");
    public static final Tag.Identified<EntityType<?>> RAIDERS = EntityTypeTags.register("raiders");
    public static final Tag.Identified<EntityType<?>> BEEHIVE_INHABITORS = EntityTypeTags.register("beehive_inhabitors");
    public static final Tag.Identified<EntityType<?>> ARROWS = EntityTypeTags.register("arrows");
    public static final Tag.Identified<EntityType<?>> IMPACT_PROJECTILES = EntityTypeTags.register("impact_projectiles");

    private static Tag.Identified<EntityType<?>> register(String id) {
        return REQUIRED_TAGS.add(id);
    }

    public static TagGroup<EntityType<?>> getTagGroup() {
        return REQUIRED_TAGS.getGroup();
    }

    public static List<? extends Tag.Identified<EntityType<?>>> method_31073() {
        return REQUIRED_TAGS.getTags();
    }
}

