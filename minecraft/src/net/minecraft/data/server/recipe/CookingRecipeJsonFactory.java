package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
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

	private CookingRecipeJsonFactory(ItemConvertible itemConvertible, Ingredient ingredient, float f, int i, CookingRecipeSerializer<?> cookingRecipeSerializer) {
		this.output = itemConvertible.asItem();
		this.input = ingredient;
		this.exp = f;
		this.time = i;
		this.serializer = cookingRecipeSerializer;
	}

	public static CookingRecipeJsonFactory create(
		Ingredient ingredient, ItemConvertible itemConvertible, float f, int i, CookingRecipeSerializer<?> cookingRecipeSerializer
	) {
		return new CookingRecipeJsonFactory(itemConvertible, ingredient, f, i, cookingRecipeSerializer);
	}

	public static CookingRecipeJsonFactory createBlasting(Ingredient ingredient, ItemConvertible itemConvertible, float f, int i) {
		return create(ingredient, itemConvertible, f, i, RecipeSerializer.BLASTING);
	}

	public static CookingRecipeJsonFactory createSmelting(Ingredient ingredient, ItemConvertible itemConvertible, float f, int i) {
		return create(ingredient, itemConvertible, f, i, RecipeSerializer.SMELTING);
	}

	public CookingRecipeJsonFactory criterion(String string, CriterionConditions criterionConditions) {
		this.builder.criterion(string, criterionConditions);
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer) {
		this.offerTo(consumer, Registry.ITEM.getId(this.output));
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.output);
		Identifier identifier2 = new Identifier(string);
		if (identifier2.equals(identifier)) {
			throw new IllegalStateException("Recipe " + identifier2 + " should remove its 'save' argument");
		} else {
			this.offerTo(consumer, identifier2);
		}
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer, Identifier identifier) {
		this.validate(identifier);
		this.builder
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(identifier))
			.rewards(AdvancementRewards.Builder.recipe(identifier))
			.criteriaMerger(CriteriaMerger.OR);
		consumer.accept(
			new CookingRecipeJsonFactory.CookingRecipeJsonProvider(
				identifier,
				this.group == null ? "" : this.group,
				this.input,
				this.output,
				this.exp,
				this.time,
				this.builder,
				new Identifier(identifier.getNamespace(), "recipes/" + this.output.getGroup().getName() + "/" + identifier.getPath()),
				(RecipeSerializer<? extends AbstractCookingRecipe>)this.serializer
			)
		);
	}

	private void validate(Identifier identifier) {
		if (this.builder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
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
			Identifier identifier,
			String string,
			Ingredient ingredient,
			Item item,
			float f,
			int i,
			Advancement.Task task,
			Identifier identifier2,
			RecipeSerializer<? extends AbstractCookingRecipe> recipeSerializer
		) {
			this.recipeId = identifier;
			this.group = string;
			this.ingredient = ingredient;
			this.result = item;
			this.experience = f;
			this.cookingTime = i;
			this.builder = task;
			this.advancementId = identifier2;
			this.cookingRecipeSerializer = recipeSerializer;
		}

		@Override
		public void serialize(JsonObject jsonObject) {
			if (!this.group.isEmpty()) {
				jsonObject.addProperty("group", this.group);
			}

			jsonObject.add("ingredient", this.ingredient.toJson());
			jsonObject.addProperty("result", Registry.ITEM.getId(this.result).toString());
			jsonObject.addProperty("experience", this.experience);
			jsonObject.addProperty("cookingtime", this.cookingTime);
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
