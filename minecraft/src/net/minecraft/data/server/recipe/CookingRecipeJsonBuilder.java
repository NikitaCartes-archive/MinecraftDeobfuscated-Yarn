package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class CookingRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RecipeCategory category;
	private final CookingRecipeCategory cookingCategory;
	private final Item output;
	private final Ingredient input;
	private final float experience;
	private final int cookingTime;
	private final Advancement.Builder advancementBuilder = Advancement.Builder.createUntelemetered();
	@Nullable
	private String group;
	private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

	private CookingRecipeJsonBuilder(
		RecipeCategory category,
		CookingRecipeCategory cookingCategory,
		ItemConvertible output,
		Ingredient input,
		float experience,
		int cookingTime,
		RecipeSerializer<? extends AbstractCookingRecipe> serializer
	) {
		this.category = category;
		this.cookingCategory = cookingCategory;
		this.output = output.asItem();
		this.input = input;
		this.experience = experience;
		this.cookingTime = cookingTime;
		this.serializer = serializer;
	}

	public static CookingRecipeJsonBuilder create(
		Ingredient input,
		RecipeCategory category,
		ItemConvertible output,
		float experience,
		int cookingTime,
		RecipeSerializer<? extends AbstractCookingRecipe> serializer
	) {
		return new CookingRecipeJsonBuilder(category, getCookingRecipeCategory(serializer, output), output, input, experience, cookingTime, serializer);
	}

	public static CookingRecipeJsonBuilder createCampfireCooking(
		Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime
	) {
		return new CookingRecipeJsonBuilder(category, CookingRecipeCategory.FOOD, output, input, experience, cookingTime, RecipeSerializer.CAMPFIRE_COOKING);
	}

	public static CookingRecipeJsonBuilder createBlasting(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime) {
		return new CookingRecipeJsonBuilder(category, getBlastingRecipeCategory(output), output, input, experience, cookingTime, RecipeSerializer.BLASTING);
	}

	public static CookingRecipeJsonBuilder createSmelting(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime) {
		return new CookingRecipeJsonBuilder(category, getSmeltingRecipeCategory(output), output, input, experience, cookingTime, RecipeSerializer.SMELTING);
	}

	public static CookingRecipeJsonBuilder createSmoking(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime) {
		return new CookingRecipeJsonBuilder(category, CookingRecipeCategory.FOOD, output, input, experience, cookingTime, RecipeSerializer.SMOKING);
	}

	public CookingRecipeJsonBuilder criterion(String string, CriterionConditions criterionConditions) {
		this.advancementBuilder.criterion(string, criterionConditions);
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
	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		this.advancementBuilder
			.parent(ROOT)
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(CriterionMerger.OR);
		exporter.accept(
			new CookingRecipeJsonBuilder.CookingRecipeJsonProvider(
				recipeId,
				this.group == null ? "" : this.group,
				this.cookingCategory,
				this.input,
				this.output,
				this.experience,
				this.cookingTime,
				this.advancementBuilder,
				recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/"),
				this.serializer
			)
		);
	}

	private static CookingRecipeCategory getSmeltingRecipeCategory(ItemConvertible output) {
		if (output.asItem().isFood()) {
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

	private void validate(Identifier recipeId) {
		if (this.advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	static class CookingRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final String group;
		private final CookingRecipeCategory category;
		private final Ingredient input;
		private final Item result;
		private final float experience;
		private final int cookingTime;
		private final Advancement.Builder advancementBuilder;
		private final Identifier advancementId;
		private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

		public CookingRecipeJsonProvider(
			Identifier recipeId,
			String group,
			CookingRecipeCategory category,
			Ingredient input,
			Item result,
			float experience,
			int cookingTime,
			Advancement.Builder advancementBuilder,
			Identifier advancementId,
			RecipeSerializer<? extends AbstractCookingRecipe> serializer
		) {
			this.recipeId = recipeId;
			this.group = group;
			this.category = category;
			this.input = input;
			this.result = result;
			this.experience = experience;
			this.cookingTime = cookingTime;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
			this.serializer = serializer;
		}

		@Override
		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			json.addProperty("category", this.category.asString());
			json.add("ingredient", this.input.toJson());
			json.addProperty("result", Registries.ITEM.getId(this.result).toString());
			json.addProperty("experience", this.experience);
			json.addProperty("cookingtime", this.cookingTime);
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return this.serializer;
		}

		@Override
		public Identifier getRecipeId() {
			return this.recipeId;
		}

		@Nullable
		@Override
		public JsonObject toAdvancementJson() {
			return this.advancementBuilder.toJson();
		}

		@Nullable
		@Override
		public Identifier getAdvancementId() {
			return this.advancementId;
		}
	}
}
