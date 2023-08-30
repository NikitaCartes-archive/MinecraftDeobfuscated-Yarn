package net.minecraft.data.server.recipe;

import net.minecraft.advancement.Advancement;

public interface RecipeExporter {
	void accept(RecipeJsonProvider recipeJsonProvider);

	Advancement.Builder getAdvancementBuilder();
}
