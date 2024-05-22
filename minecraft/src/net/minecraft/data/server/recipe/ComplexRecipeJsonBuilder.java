package net.minecraft.data.server.recipe;

import java.util.function.Function;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;

public class ComplexRecipeJsonBuilder {
	private final Function<CraftingRecipeCategory, Recipe<?>> recipeFactory;

	public ComplexRecipeJsonBuilder(Function<CraftingRecipeCategory, Recipe<?>> recipeFactory) {
		this.recipeFactory = recipeFactory;
	}

	public static ComplexRecipeJsonBuilder create(Function<CraftingRecipeCategory, Recipe<?>> recipeFactory) {
		return new ComplexRecipeJsonBuilder(recipeFactory);
	}

	public void offerTo(RecipeExporter exporter, String id) {
		this.offerTo(exporter, Identifier.of(id));
	}

	public void offerTo(RecipeExporter exporter, Identifier id) {
		exporter.accept(id, (Recipe<?>)this.recipeFactory.apply(CraftingRecipeCategory.MISC), null);
	}
}
