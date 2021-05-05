package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CookingRecipeJsonFactory implements CraftingRecipeJsonFactory {
	private final Item output;
	private final Ingredient input;
	private final float experience;
	private final int cookingTime;
	private final Advancement.Task builder = Advancement.Task.create();
	@Nullable
	private String group;
	private final CookingRecipeSerializer<?> serializer;

	private CookingRecipeJsonFactory(ItemConvertible output, Ingredient input, float experience, int cookingTime, CookingRecipeSerializer<?> serializer) {
		this.output = output.asItem();
		this.input = input;
		this.experience = experience;
		this.cookingTime = cookingTime;
		this.serializer = serializer;
	}

	public static CookingRecipeJsonFactory create(
		Ingredient ingredient, ItemConvertible result, float experience, int cookingTime, CookingRecipeSerializer<?> serializer
	) {
		return new CookingRecipeJsonFactory(result, ingredient, experience, cookingTime, serializer);
	}

	public static CookingRecipeJsonFactory create(Ingredient result, ItemConvertible ingredient, float experience, int cookingTime) {
		return create(result, ingredient, experience, cookingTime, RecipeSerializer.CAMPFIRE_COOKING);
	}

	public static CookingRecipeJsonFactory createBlasting(Ingredient ingredient, ItemConvertible result, float experience, int cookingTime) {
		return create(ingredient, result, experience, cookingTime, RecipeSerializer.BLASTING);
	}

	public static CookingRecipeJsonFactory createSmelting(Ingredient ingredient, ItemConvertible result, float experience, int cookingTime) {
		return create(ingredient, result, experience, cookingTime, RecipeSerializer.SMELTING);
	}

	public static CookingRecipeJsonFactory createSmoking(Ingredient result, ItemConvertible ingredient, float experience, int cookingTime) {
		return create(result, ingredient, experience, cookingTime, RecipeSerializer.SMOKING);
	}

	public CookingRecipeJsonFactory criterion(String string, CriterionConditions criterionConditions) {
		this.builder.criterion(string, criterionConditions);
		return this;
	}

	public CookingRecipeJsonFactory group(@Nullable String string) {
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
		this.builder
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(CriterionMerger.OR);
		exporter.accept(
			new CookingRecipeJsonFactory.CookingRecipeJsonProvider(
				recipeId,
				this.group == null ? "" : this.group,
				this.input,
				this.output,
				this.experience,
				this.cookingTime,
				this.builder,
				new Identifier(recipeId.getNamespace(), "recipes/" + this.output.getGroup().getName() + "/" + recipeId.getPath()),
				(RecipeSerializer<? extends AbstractCookingRecipe>)this.serializer
			)
		);
	}

	private void validate(Identifier recipeId) {
		if (this.builder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static class CookingRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final String group;
		private final Ingredient input;
		private final Item result;
		private final float experience;
		private final int cookingTime;
		private final Advancement.Task builder;
		private final Identifier advancementId;
		private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

		public CookingRecipeJsonProvider(
			Identifier recipeId,
			String group,
			Ingredient input,
			Item result,
			float experience,
			int cookingTime,
			Advancement.Task builder,
			Identifier advancementId,
			RecipeSerializer<? extends AbstractCookingRecipe> serializer
		) {
			this.recipeId = recipeId;
			this.group = group;
			this.input = input;
			this.result = result;
			this.experience = experience;
			this.cookingTime = cookingTime;
			this.builder = builder;
			this.advancementId = advancementId;
			this.serializer = serializer;
		}

		@Override
		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			json.add("ingredient", this.input.toJson());
			json.addProperty("result", Registry.ITEM.getId(this.result).toString());
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
			return this.builder.toJson();
		}

		@Nullable
		@Override
		public Identifier getAdvancementId() {
			return this.advancementId;
		}
	}
}
