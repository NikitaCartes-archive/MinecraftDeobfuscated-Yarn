/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;

public abstract class RecipeJsonBuilder {
    protected static CraftingRecipeCategory getCraftingCategory(RecipeCategory category) {
        return switch (category) {
            case RecipeCategory.BUILDING_BLOCKS -> CraftingRecipeCategory.BUILDING;
            case RecipeCategory.TOOLS, RecipeCategory.COMBAT -> CraftingRecipeCategory.EQUIPMENT;
            case RecipeCategory.REDSTONE -> CraftingRecipeCategory.REDSTONE;
            default -> CraftingRecipeCategory.MISC;
        };
    }

    protected static abstract class CraftingRecipeJsonProvider
    implements RecipeJsonProvider {
        private final CraftingRecipeCategory craftingCategory;

        protected CraftingRecipeJsonProvider(CraftingRecipeCategory craftingCategory) {
            this.craftingCategory = craftingCategory;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("category", this.craftingCategory.asString());
        }
    }
}

