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
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.PoisonousPotatoCuttingRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

public class SingleItemRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RecipeCategory category;
	private final Item output;
	private final Ingredient input;
	private final int count;
	private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap();
	@Nullable
	private String group;
	private final CuttingRecipe.RecipeFactory<?> recipeFactory;

	public SingleItemRecipeJsonBuilder(RecipeCategory category, CuttingRecipe.RecipeFactory<?> recipeFactory, Ingredient input, ItemConvertible output, int count) {
		this.category = category;
		this.recipeFactory = recipeFactory;
		this.output = output.asItem();
		this.input = input;
		this.count = count;
	}

	public static SingleItemRecipeJsonBuilder createStonecutting(Ingredient input, RecipeCategory category, ItemConvertible output) {
		return new SingleItemRecipeJsonBuilder(category, StonecuttingRecipe::new, input, output, 1);
	}

	public static SingleItemRecipeJsonBuilder createStonecutting(Ingredient input, RecipeCategory category, ItemConvertible output, int count) {
		return new SingleItemRecipeJsonBuilder(category, StonecuttingRecipe::new, input, output, count);
	}

	public static SingleItemRecipeJsonBuilder method_59466(Ingredient ingredient, RecipeCategory recipeCategory, ItemConvertible itemConvertible, int i) {
		return new SingleItemRecipeJsonBuilder(recipeCategory, PoisonousPotatoCuttingRecipe::new, ingredient, itemConvertible, i);
	}

	public SingleItemRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
		this.criteria.put(string, advancementCriterion);
		return this;
	}

	public SingleItemRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	@Override
	public Item getOutputItem() {
		return this.output;
	}

	@Override
	public void offerTo(RecipeExporter exporter, Identifier recipeId) {
		this.validate(recipeId);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		this.criteria.forEach(builder::criterion);
		CuttingRecipe cuttingRecipe = this.recipeFactory
			.create((String)Objects.requireNonNullElse(this.group, ""), this.input, new ItemStack(this.output, this.count));
		exporter.accept(recipeId, cuttingRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
	}

	private void validate(Identifier recipeId) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}
}
