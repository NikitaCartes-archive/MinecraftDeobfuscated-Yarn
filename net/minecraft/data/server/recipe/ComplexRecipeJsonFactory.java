/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ComplexRecipeJsonFactory {
    private final SpecialRecipeSerializer<?> serializer;

    public ComplexRecipeJsonFactory(SpecialRecipeSerializer<?> specialRecipeSerializer) {
        this.serializer = specialRecipeSerializer;
    }

    public static ComplexRecipeJsonFactory create(SpecialRecipeSerializer<?> specialRecipeSerializer) {
        return new ComplexRecipeJsonFactory(specialRecipeSerializer);
    }

    public void offerTo(Consumer<RecipeJsonProvider> consumer, final String string) {
        consumer.accept(new RecipeJsonProvider(){

            @Override
            public void serialize(JsonObject jsonObject) {
            }

            @Override
            public RecipeSerializer<?> getSerializer() {
                return ComplexRecipeJsonFactory.this.serializer;
            }

            @Override
            public Identifier getRecipeId() {
                return new Identifier(string);
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

