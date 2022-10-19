/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe.book;

import net.minecraft.util.StringIdentifiable;

public enum CookingRecipeCategory implements StringIdentifiable
{
    FOOD("food"),
    BLOCKS("blocks"),
    MISC("misc");

    public static final StringIdentifiable.Codec<CookingRecipeCategory> CODEC;
    private final String id;

    private CookingRecipeCategory(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    static {
        CODEC = StringIdentifiable.createCodec(CookingRecipeCategory::values);
    }
}

