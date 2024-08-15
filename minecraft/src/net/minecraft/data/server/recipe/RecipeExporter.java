package net.minecraft.data.server.recipe;

import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public interface RecipeExporter {
	void accept(Identifier recipeId, Recipe<?> recipe, @Nullable AdvancementEntry advancement);

	Advancement.Builder getAdvancementBuilder();

	void addRootAdvancement();
}
