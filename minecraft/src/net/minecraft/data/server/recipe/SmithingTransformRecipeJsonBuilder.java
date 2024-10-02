package net.minecraft.data.server.recipe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class SmithingTransformRecipeJsonBuilder {
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;
	private final RecipeCategory category;
	private final Item result;
	private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap();

	public SmithingTransformRecipeJsonBuilder(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, Item result) {
		this.category = category;
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
	}

	public static SmithingTransformRecipeJsonBuilder create(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, Item result) {
		return new SmithingTransformRecipeJsonBuilder(template, base, addition, category, result);
	}

	public SmithingTransformRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
		this.criteria.put(name, criterion);
		return this;
	}

	public void offerTo(RecipeExporter exporter, String recipeId) {
		this.offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(recipeId)));
	}

	public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
		this.validate(recipeKey);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
			.rewards(AdvancementRewards.Builder.recipe(recipeKey))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		this.criteria.forEach(builder::criterion);
		SmithingTransformRecipe smithingTransformRecipe = new SmithingTransformRecipe(
			Optional.of(this.template), Optional.of(this.base), Optional.of(this.addition), new ItemStack(this.result)
		);
		exporter.accept(recipeKey, smithingTransformRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + this.category.getName() + "/")));
	}

	private void validate(RegistryKey<Recipe<?>> recipeKey) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeKey.getValue());
		}
	}
}
