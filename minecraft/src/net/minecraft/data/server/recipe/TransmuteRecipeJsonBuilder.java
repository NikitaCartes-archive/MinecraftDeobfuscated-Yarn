package net.minecraft.data.server.recipe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.TransmuteRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public class TransmuteRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RecipeCategory category;
	private final RegistryEntry<Item> result;
	private final Ingredient input;
	private final Ingredient material;
	private final Map<String, AdvancementCriterion<?>> advancementBuilder = new LinkedHashMap();
	@Nullable
	private String group;

	private TransmuteRecipeJsonBuilder(RecipeCategory category, RegistryEntry<Item> result, Ingredient input, Ingredient material) {
		this.category = category;
		this.result = result;
		this.input = input;
		this.material = material;
	}

	public static TransmuteRecipeJsonBuilder create(RecipeCategory category, Ingredient input, Ingredient material, Item result) {
		return new TransmuteRecipeJsonBuilder(category, result.getRegistryEntry(), input, material);
	}

	public TransmuteRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
		this.advancementBuilder.put(string, advancementCriterion);
		return this;
	}

	public TransmuteRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	@Override
	public Item getOutputItem() {
		return this.result.value();
	}

	@Override
	public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
		this.validate(recipeKey);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
			.rewards(AdvancementRewards.Builder.recipe(recipeKey))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		this.advancementBuilder.forEach(builder::criterion);
		TransmuteRecipe transmuteRecipe = new TransmuteRecipe(
			(String)Objects.requireNonNullElse(this.group, ""), CraftingRecipeJsonBuilder.toCraftingCategory(this.category), this.input, this.material, this.result
		);
		exporter.accept(recipeKey, transmuteRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + this.category.getName() + "/")));
	}

	private void validate(RegistryKey<Recipe<?>> recipeKey) {
		if (this.advancementBuilder.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeKey.getValue());
		}
	}
}
