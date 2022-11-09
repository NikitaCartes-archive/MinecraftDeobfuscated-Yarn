/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry.tag;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class PaintingVariantTags {
    public static final TagKey<PaintingVariant> PLACEABLE = PaintingVariantTags.of("placeable");

    private PaintingVariantTags() {
    }

    private static TagKey<PaintingVariant> of(String id) {
        return TagKey.of(RegistryKeys.PAINTING_VARIANT, new Identifier(id));
    }
}

