/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.data.server.recipe.RecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ComplexRecipeJsonBuilder
extends RecipeJsonBuilder {
    final RecipeSerializer<?> serializer;

    public ComplexRecipeJsonBuilder(RecipeSerializer<?> serializer) {
        this.serializer = serializer;
    }

    public static ComplexRecipeJsonBuilder create(RecipeSerializer<? extends CraftingRecipe> serializer) {
        return new ComplexRecipeJsonBuilder(serializer);
    }

    public void offerTo(Consumer<RecipeJsonProvider> exporter, final String recipeId) {
        exporter.accept(new RecipeJsonBuilder.CraftingRecipeJsonProvider(CraftingRecipeCategory.MISC){

            @Override
            public RecipeSerializer<?> getSerializer() {
                return ComplexRecipeJsonBuilder.this.serializer;
            }

            @Override
            public Identifier getRecipeId() {
                return new Identifier(recipeId);
            }

            @Override
            @Nullable
            public JsonObject toAdvancementJson() {
                return null;
            }

            @Override
            public Identifier getAdvancementId() {
                return new Identifier("");
            }
        });
    }
}

