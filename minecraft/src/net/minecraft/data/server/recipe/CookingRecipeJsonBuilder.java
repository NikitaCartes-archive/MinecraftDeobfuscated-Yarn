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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;

public class CookingRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RecipeCategory category;
	private final CookingRecipeCategory cookingCategory;
	private final Item output;
	private final Ingredient input;
	private final float experience;
	private final int cookingTime;
	private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap();
	@Nullable
	private String group;
	private final AbstractCookingRecipe.RecipeFactory<?> recipeFactory;

	private CookingRecipeJsonBuilder(
		RecipeCategory category,
		CookingRecipeCategory cookingCategory,
		ItemConvertible output,
		Ingredient input,
		float experience,
		int cookingTime,
		AbstractCookingRecipe.RecipeFactory<?> recipeFactory
	) {
		this.category = category;
		this.cookingCategory = cookingCategory;
		this.output = output.asItem();
		this.input = input;
		this.experience = experience;
		this.cookingTime = cookingTime;
		this.recipeFactory = recipeFactory;
	}

	public static <T extends AbstractCookingRecipe> CookingRecipeJsonBuilder create(
		Ingredient input,
		RecipeCategory category,
		ItemConvertible output,
		float experience,
		int cookingTime,
		RecipeSerializer<T> serializer,
		AbstractCookingRecipe.RecipeFactory<T> recipeFactory
	) {
		return new CookingRecipeJsonBuilder(category, getCookingRecipeCategory(serializer, output), output, input, experience, cookingTime, recipeFactory);
	}

	public static CookingRecipeJsonBuilder createCampfireCooking(
		Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime
	) {
		return new CookingRecipeJsonBuilder(category, CookingRecipeCategory.FOOD, output, input, experience, cookingTime, CampfireCookingRecipe::new);
	}

	public static CookingRecipeJsonBuilder createBlasting(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime) {
		return new CookingRecipeJsonBuilder(category, getBlastingRecipeCategory(output), output, input, experience, cookingTime, BlastingRecipe::new);
	}

	public static CookingRecipeJsonBuilder createSmelting(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime) {
		return new CookingRecipeJsonBuilder(category, getSmeltingRecipeCategory(output), output, input, experience, cookingTime, SmeltingRecipe::new);
	}

	public static CookingRecipeJsonBuilder createSmoking(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime) {
		return new CookingRecipeJsonBuilder(category, CookingRecipeCategory.FOOD, output, input, experience, cookingTime, SmokingRecipe::new);
	}

	public CookingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
		this.criteria.put(string, advancementCriterion);
		return this;
	}

	public CookingRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	@Override
	public Item getOutputItem() {
		return this.output;
	}

	@Override
	public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
		this.validate(recipeKey);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
			.rewards(AdvancementRewards.Builder.recipe(recipeKey))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		this.criteria.forEach(builder::criterion);
		AbstractCookingRecipe abstractCookingRecipe = this.recipeFactory
			.create((String)Objects.requireNonNullElse(this.group, ""), this.cookingCategory, this.input, new ItemStack(this.output), this.experience, this.cookingTime);
		exporter.accept(recipeKey, abstractCookingRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + this.category.getName() + "/")));
	}

	private static CookingRecipeCategory getSmeltingRecipeCategory(ItemConvertible output) {
		if (output.asItem().getComponents().contains(DataComponentTypes.FOOD)) {
			return CookingRecipeCategory.FOOD;
		} else {
			return output.asItem() instanceof BlockItem ? CookingRecipeCategory.BLOCKS : CookingRecipeCategory.MISC;
		}
	}

	private static CookingRecipeCategory getBlastingRecipeCategory(ItemConvertible output) {
		return output.asItem() instanceof BlockItem ? CookingRecipeCategory.BLOCKS : CookingRecipeCategory.MISC;
	}

	private static CookingRecipeCategory getCookingRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> serializer, ItemConvertible output) {
		if (serializer == RecipeSerializer.SMELTING) {
			return getSmeltingRecipeCategory(output);
		} else if (serializer == RecipeSerializer.BLASTING) {
			return getBlastingRecipeCategory(output);
		} else if (serializer != RecipeSerializer.SMOKING && serializer != RecipeSerializer.CAMPFIRE_COOKING) {
			throw new IllegalStateException("Unknown cooking recipe type");
		} else {
			return CookingRecipeCategory.FOOD;
		}
	}

	private void validate(RegistryKey<Recipe<?>> recipeKey) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeKey.getValue());
		}
	}
}
