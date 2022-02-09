package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;

public class ComplexRecipeJsonBuilder {
	final SpecialRecipeSerializer<?> serializer;

	public ComplexRecipeJsonBuilder(SpecialRecipeSerializer<?> serializer) {
		this.serializer = serializer;
	}

	public static ComplexRecipeJsonBuilder create(SpecialRecipeSerializer<?> serializer) {
		return new ComplexRecipeJsonBuilder(serializer);
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipeId) {
		exporter.accept(new RecipeJsonProvider() {
			@Override
			public void serialize(JsonObject json) {
			}

			@Override
			public RecipeSerializer<?> getSerializer() {
				return ComplexRecipeJsonBuilder.this.serializer;
			}

			@Override
			public Identifier getRecipeId() {
				return new Identifier(recipeId);
			}

			@Nullable
			@Override
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
