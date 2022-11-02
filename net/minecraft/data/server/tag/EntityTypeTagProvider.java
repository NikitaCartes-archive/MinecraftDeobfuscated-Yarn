/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryWrapper;

public class EntityTypeTagProvider
extends ValueLookupTagProvider<EntityType<?>> {
    public EntityTypeTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, Registry.ENTITY_TYPE_KEY, registryLookupFuture, entityType -> entityType.getRegistryEntry().registryKey());
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)EntityTypeTags.SKELETONS)).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)EntityTypeTags.RAIDERS)).add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)EntityTypeTags.BEEHIVE_INHABITORS)).add(EntityType.BEE);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)EntityTypeTags.ARROWS)).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
        ((ValueLookupTagProvider.ObjectBuilder)((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)EntityTypeTags.IMPACT_PROJECTILES)).addTag((TagKey)EntityTypeTags.ARROWS)).add(EntityType.SNOWBALL, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.EGG, EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.WITHER_SKULL);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)).add(EntityType.RABBIT, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.FOX);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)EntityTypeTags.AXOLOTL_HUNT_TARGETS)).add(EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.COD, EntityType.SQUID, EntityType.GLOW_SQUID, EntityType.TADPOLE);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES)).add(EntityType.DROWNED, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)).add(EntityType.STRAY, EntityType.POLAR_BEAR, EntityType.SNOW_GOLEM, EntityType.WITHER);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)).add(EntityType.STRIDER, EntityType.BLAZE, EntityType.MAGMA_CUBE);
        ((ValueLookupTagProvider.ObjectBuilder)this.getOrCreateTagBuilder((TagKey)EntityTypeTags.FROG_FOOD)).add(EntityType.SLIME, EntityType.MAGMA_CUBE);
    }
}

