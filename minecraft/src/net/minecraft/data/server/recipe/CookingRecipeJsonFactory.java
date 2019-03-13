package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.cooking.CookingRecipe;
import net.minecraft.recipe.cooking.CookingRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CookingRecipeJsonFactory {
	private final Item output;
	private final Ingredient input;
	private final float exp;
	private final int time;
	private final SimpleAdvancement.Builder field_11416 = SimpleAdvancement.Builder.create();
	private String group;
	private final CookingRecipeSerializer<?> serializer;

	private CookingRecipeJsonFactory(ItemProvider itemProvider, Ingredient ingredient, float f, int i, CookingRecipeSerializer<?> cookingRecipeSerializer) {
		this.output = itemProvider.getItem();
		this.input = ingredient;
		this.exp = f;
		this.time = i;
		this.serializer = cookingRecipeSerializer;
	}

	public static CookingRecipeJsonFactory create(
		Ingredient ingredient, ItemProvider itemProvider, float f, int i, CookingRecipeSerializer<?> cookingRecipeSerializer
	) {
		return new CookingRecipeJsonFactory(itemProvider, ingredient, f, i, cookingRecipeSerializer);
	}

	public static CookingRecipeJsonFactory createBlasting(Ingredient ingredient, ItemProvider itemProvider, float f, int i) {
		return create(ingredient, itemProvider, f, i, RecipeSerializer.field_17084);
	}

	public static CookingRecipeJsonFactory createSmelting(Ingredient ingredient, ItemProvider itemProvider, float f, int i) {
		return create(ingredient, itemProvider, f, i, RecipeSerializer.field_9042);
	}

	public CookingRecipeJsonFactory method_10469(String string, CriterionConditions criterionConditions) {
		this.field_11416.method_709(string, criterionConditions);
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer) {
		this.method_10468(consumer, Registry.ITEM.method_10221(this.output));
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
		Identifier identifier = Registry.ITEM.method_10221(this.output);
		Identifier identifier2 = new Identifier(string);
		if (identifier2.equals(identifier)) {
			throw new IllegalStateException("Recipe " + identifier2 + " should remove its 'save' argument");
		} else {
			this.method_10468(consumer, identifier2);
		}
	}

	public void method_10468(Consumer<RecipeJsonProvider> consumer, Identifier identifier) {
		this.method_10471(identifier);
		this.field_11416
			.method_708(new Identifier("recipes/root"))
			.method_709("has_the_recipe", new RecipeUnlockedCriterion.Conditions(identifier))
			.method_703(AdvancementRewards.Builder.recipe(identifier))
			.criteriaMerger(CriteriaMerger.OR);
		consumer.accept(
			new CookingRecipeJsonFactory.CookingRecipeJsonProvider(
				identifier,
				this.group == null ? "" : this.group,
				this.input,
				this.output,
				this.exp,
				this.time,
				this.field_11416,
				new Identifier(identifier.getNamespace(), "recipes/" + this.output.getItemGroup().getName() + "/" + identifier.getPath()),
				(RecipeSerializer<? extends CookingRecipe>)this.serializer
			)
		);
	}

	private void method_10471(Identifier identifier) {
		if (this.field_11416.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class CookingRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier field_11424;
		private final String group;
		private final Ingredient ingredient;
		private final Item result;
		private final float experience;
		private final int cookingTime;
		private final SimpleAdvancement.Builder field_11423;
		private final Identifier field_11427;
		private final RecipeSerializer<? extends CookingRecipe> cookingRecipeSerializer;

		public CookingRecipeJsonProvider(
			Identifier identifier,
			String string,
			Ingredient ingredient,
			Item item,
			float f,
			int i,
			SimpleAdvancement.Builder builder,
			Identifier identifier2,
			RecipeSerializer<? extends CookingRecipe> recipeSerializer
		) {
			this.field_11424 = identifier;
			this.group = string;
			this.ingredient = ingredient;
			this.result = item;
			this.experience = f;
			this.cookingTime = i;
			this.field_11423 = builder;
			this.field_11427 = identifier2;
			this.cookingRecipeSerializer = recipeSerializer;
		}

		@Override
		public void serialize(JsonObject jsonObject) {
			if (!this.group.isEmpty()) {
				jsonObject.addProperty("group", this.group);
			}

			jsonObject.add("ingredient", this.ingredient.toJson());
			jsonObject.addProperty("result", Registry.ITEM.method_10221(this.result).toString());
			jsonObject.addProperty("experience", this.experience);
			jsonObject.addProperty("cookingtime", this.cookingTime);
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return this.cookingRecipeSerializer;
		}

		@Override
		public Identifier method_10417() {
			return this.field_11424;
		}

		@Nullable
		@Override
		public JsonObject toAdvancementJson() {
			return this.field_11423.toJson();
		}

		@Nullable
		@Override
		public Identifier method_10418() {
			return this.field_11427;
		}
	}
}
