/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PaintingVariantTags {
    public static final TagKey<PaintingVariant> PLACEABLE = PaintingVariantTags.of("placeable");

    private PaintingVariantTags() {
    }

    private static TagKey<PaintingVariant> of(String id) {
        return TagKey.of(Registry.PAINTING_VARIANT_KEY, new Identifier(id));
    }
}

