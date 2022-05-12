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

public class CookingRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final Item output;
	private final Ingredient input;
	private final float experience;
	private final int cookingTime;
	private final Advancement.Builder advancementBuilder = Advancement.Builder.create();
	@Nullable
	private String group;
	private final CookingRecipeSerializer<?> serializer;

	private CookingRecipeJsonBuilder(ItemConvertible output, Ingredient input, float experience, int cookingTime, CookingRecipeSerializer<?> serializer) {
		this.output = output.asItem();
		this.input = input;
		this.experience = experience;
		this.cookingTime = cookingTime;
		this.serializer = serializer;
	}

	public static CookingRecipeJsonBuilder create(
		Ingredient ingredient, ItemConvertible result, float experience, int cookingTime, CookingRecipeSerializer<?> serializer
	) {
		return new CookingRecipeJsonBuilder(result, ingredient, experience, cookingTime, serializer);
	}

	public static CookingRecipeJsonBuilder create(Ingredient result, ItemConvertible ingredient, float experience, int cookingTime) {
		return create(result, ingredient, experience, cookingTime, RecipeSerializer.CAMPFIRE_COOKING);
	}

	public static CookingRecipeJsonBuilder createBlasting(Ingredient ingredient, ItemConvertible result, float experience, int cookingTime) {
		return create(ingredient, result, experience, cookingTime, RecipeSerializer.BLASTING);
	}

	public static CookingRecipeJsonBuilder createSmelting(Ingredient ingredient, ItemConvertible result, float experience, int cookingTime) {
		return create(ingredient, result, experience, cookingTime, RecipeSerializer.SMELTING);
	}

	public static CookingRecipeJsonBuilder createSmoking(Ingredient result, ItemConvertible ingredient, float experience, int cookingTime) {
		return create(result, ingredient, experience, cookingTime, RecipeSerializer.SMOKING);
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
			.parent(field_39377)
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(CriterionMerger.OR);
		exporter.accept(
			new CookingRecipeJsonBuilder.CookingRecipeJsonProvider(
				recipeId,
				this.group == null ? "" : this.group,
				this.input,
				this.output,
				this.experience,
				this.cookingTime,
				this.advancementBuilder,
				new Identifier(recipeId.getNamespace(), "recipes/" + this.output.getGroup().getName() + "/" + recipeId.getPath()),
				(RecipeSerializer<? extends AbstractCookingRecipe>)this.serializer
			)
		);
	}

	private void validate(Identifier recipeId) {
		if (this.advancementBuilder.getCriteria().isEmpty()) {
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
		private final Advancement.Builder advancementBuilder;
		private final Identifier advancementId;
		private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

		public CookingRecipeJsonProvider(
			Identifier recipeId,
			String group,
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
			return this.advancementBuilder.toJson();
		}

		@Nullable
		@Override
		public Identifier getAdvancementId() {
			return this.advancementId;
		}
	}
}
