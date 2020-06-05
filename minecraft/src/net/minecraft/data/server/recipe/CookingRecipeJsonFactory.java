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

public class CookingRecipeJsonFactory {
	private final Item output;
	private final Ingredient input;
	private final float exp;
	private final int time;
	private final Advancement.Task builder = Advancement.Task.create();
	private String group;
	private final CookingRecipeSerializer<?> serializer;

	private CookingRecipeJsonFactory(ItemConvertible ouptut, Ingredient input, float exp, int time, CookingRecipeSerializer<?> serializer) {
		this.output = ouptut.asItem();
		this.input = input;
		this.exp = exp;
		this.time = time;
		this.serializer = serializer;
	}

	public static CookingRecipeJsonFactory create(Ingredient input, ItemConvertible output, float exp, int time, CookingRecipeSerializer<?> serializer) {
		return new CookingRecipeJsonFactory(output, input, exp, time, serializer);
	}

	public static CookingRecipeJsonFactory createBlasting(Ingredient input, ItemConvertible output, float exp, int time) {
		return create(input, output, exp, time, RecipeSerializer.BLASTING);
	}

	public static CookingRecipeJsonFactory createSmelting(Ingredient input, ItemConvertible output, float exp, int time) {
		return create(input, output, exp, time, RecipeSerializer.SMELTING);
	}

	public CookingRecipeJsonFactory criterion(String criterionName, CriterionConditions conditions) {
		this.builder.criterion(criterionName, conditions);
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter) {
		this.offerTo(exporter, Registry.ITEM.getId(this.output));
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipeIdStr) {
		Identifier identifier = Registry.ITEM.getId(this.output);
		Identifier identifier2 = new Identifier(recipeIdStr);
		if (identifier2.equals(identifier)) {
			throw new IllegalStateException("Recipe " + identifier2 + " should remove its 'save' argument");
		} else {
			this.offerTo(exporter, identifier2);
		}
	}

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
				this.exp,
				this.time,
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
		private final Ingredient ingredient;
		private final Item result;
		private final float experience;
		private final int cookingTime;
		private final Advancement.Task builder;
		private final Identifier advancementId;
		private final RecipeSerializer<? extends AbstractCookingRecipe> cookingRecipeSerializer;

		public CookingRecipeJsonProvider(
			Identifier recipeId,
			String group,
			Ingredient input,
			Item output,
			float exp,
			int time,
			Advancement.Task builder,
			Identifier advancementId,
			RecipeSerializer<? extends AbstractCookingRecipe> serializer
		) {
			this.recipeId = recipeId;
			this.group = group;
			this.ingredient = input;
			this.result = output;
			this.experience = exp;
			this.cookingTime = time;
			this.builder = builder;
			this.advancementId = advancementId;
			this.cookingRecipeSerializer = serializer;
		}

		@Override
		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			json.add("ingredient", this.ingredient.toJson());
			json.addProperty("result", Registry.ITEM.getId(this.result).toString());
			json.addProperty("experience", this.experience);
			json.addProperty("cookingtime", this.cookingTime);
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return this.cookingRecipeSerializer;
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
