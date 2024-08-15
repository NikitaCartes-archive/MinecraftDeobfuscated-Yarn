package net.minecraft.data.server.recipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ShapelessRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RegistryEntryLookup<Item> registryLookup;
	private final RecipeCategory category;
	private final ItemStack output;
	private final List<Ingredient> inputs = new ArrayList();
	private final Map<String, AdvancementCriterion<?>> advancementBuilder = new LinkedHashMap();
	@Nullable
	private String group;

	private ShapelessRecipeJsonBuilder(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemStack output) {
		this.registryLookup = registryLookup;
		this.category = category;
		this.output = output;
	}

	public static ShapelessRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemStack output) {
		return new ShapelessRecipeJsonBuilder(registryLookup, category, output);
	}

	public static ShapelessRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output) {
		return create(registryLookup, category, output, 1);
	}

	public static ShapelessRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output, int count) {
		return new ShapelessRecipeJsonBuilder(registryLookup, category, output.asItem().getDefaultStack().copyWithCount(count));
	}

	public ShapelessRecipeJsonBuilder input(TagKey<Item> tag) {
		return this.input(Ingredient.fromTag(this.registryLookup.getOrThrow(tag)));
	}

	public ShapelessRecipeJsonBuilder input(ItemConvertible item) {
		return this.input(item, 1);
	}

	public ShapelessRecipeJsonBuilder input(ItemConvertible item, int amount) {
		for (int i = 0; i < amount; i++) {
			this.input(Ingredient.ofItem(item));
		}

		return this;
	}

	public ShapelessRecipeJsonBuilder input(Ingredient ingredient) {
		return this.input(ingredient, 1);
	}

	public ShapelessRecipeJsonBuilder input(Ingredient ingredient, int amount) {
		for (int i = 0; i < amount; i++) {
			this.inputs.add(ingredient);
		}

		return this;
	}

	public ShapelessRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
		this.advancementBuilder.put(string, advancementCriterion);
		return this;
	}

	public ShapelessRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	@Override
	public Item getOutputItem() {
		return this.output.getItem();
	}

	@Override
	public void offerTo(RecipeExporter exporter, Identifier recipeId) {
		this.validate(recipeId);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		this.advancementBuilder.forEach(builder::criterion);
		ShapelessRecipe shapelessRecipe = new ShapelessRecipe(
			(String)Objects.requireNonNullElse(this.group, ""), CraftingRecipeJsonBuilder.toCraftingCategory(this.category), this.output, this.inputs
		);
		exporter.accept(recipeId, shapelessRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
	}

	private void validate(Identifier recipeId) {
		if (this.advancementBuilder.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}
}
