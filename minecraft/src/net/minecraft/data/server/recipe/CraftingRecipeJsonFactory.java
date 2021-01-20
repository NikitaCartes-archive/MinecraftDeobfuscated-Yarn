package net.minecraft.data.server.recipe;

import java.util.function.Consumer;
import net.minecraft.advancement.criterion.CriterionConditions;

public interface CraftingRecipeJsonFactory {
	CraftingRecipeJsonFactory criterion(String name, CriterionConditions conditions);

	CraftingRecipeJsonFactory group(String group);

	void offerTo(Consumer<RecipeJsonProvider> exporter);
}
