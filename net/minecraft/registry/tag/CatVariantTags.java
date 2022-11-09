/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry.tag;

import net.minecraft.entity.passive.CatVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class CatVariantTags {
    public static final TagKey<CatVariant> DEFAULT_SPAWNS = CatVariantTags.of("default_spawns");
    public static final TagKey<CatVariant> FULL_MOON_SPAWNS = CatVariantTags.of("full_moon_spawns");

    private CatVariantTags() {
    }

    private static TagKey<CatVariant> of(String id) {
        return TagKey.of(RegistryKeys.CAT_VARIANT, new Identifier(id));
    }
}

