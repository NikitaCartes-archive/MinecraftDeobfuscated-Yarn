package net.minecraft.data.server.recipe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;

public class SmithingTrimRecipeJsonBuilder {
	private final RecipeCategory category;
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;
	private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap();

	public SmithingTrimRecipeJsonBuilder(RecipeCategory category, Ingredient template, Ingredient base, Ingredient addition) {
		this.category = category;
		this.template = template;
		this.base = base;
		this.addition = addition;
	}

	public static SmithingTrimRecipeJsonBuilder create(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category) {
		return new SmithingTrimRecipeJsonBuilder(category, template, base, addition);
	}

	public SmithingTrimRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
		this.criteria.put(name, criterion);
		return this;
	}

	public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
		this.validate(recipeKey);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
			.rewards(AdvancementRewards.Builder.recipe(recipeKey))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		this.criteria.forEach(builder::criterion);
		SmithingTrimRecipe smithingTrimRecipe = new SmithingTrimRecipe(Optional.of(this.template), Optional.of(this.base), Optional.of(this.addition));
		exporter.accept(recipeKey, smithingTrimRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + this.category.getName() + "/")));
	}

	private void validate(RegistryKey<Recipe<?>> recipeKey) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeKey.getValue());
		}
	}
}
