/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe.book;

import net.minecraft.util.StringIdentifiable;

public enum CraftingRecipeCategory implements StringIdentifiable
{
    BUILDING("building"),
    REDSTONE("redstone"),
    EQUIPMENT("equipment"),
    MISC("misc");

    public static final StringIdentifiable.Codec<CraftingRecipeCategory> CODEC;
    private final String id;

    private CraftingRecipeCategory(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    static {
        CODEC = StringIdentifiable.createCodec(CraftingRecipeCategory::values);
    }
}

