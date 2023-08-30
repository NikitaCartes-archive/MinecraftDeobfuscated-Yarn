package net.minecraft.data.server.recipe;

import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;

public class ComplexRecipeJsonBuilder extends RecipeJsonBuilder {
	final RecipeSerializer<?> serializer;

	public ComplexRecipeJsonBuilder(RecipeSerializer<?> serializer) {
		this.serializer = serializer;
	}

	public static ComplexRecipeJsonBuilder create(RecipeSerializer<? extends CraftingRecipe> serializer) {
		return new ComplexRecipeJsonBuilder(serializer);
	}

	public void offerTo(RecipeExporter exporter, String id) {
		this.offerTo(exporter, new Identifier(id));
	}

	public void offerTo(RecipeExporter exporter, Identifier id) {
		exporter.accept(new RecipeJsonBuilder.CraftingRecipeJsonProvider(CraftingRecipeCategory.MISC) {
			@Override
			public RecipeSerializer<?> serializer() {
				return ComplexRecipeJsonBuilder.this.serializer;
			}

			@Override
			public Identifier id() {
				return id;
			}

			@Nullable
			@Override
			public AdvancementEntry advancement() {
				return null;
			}
		});
	}
}
