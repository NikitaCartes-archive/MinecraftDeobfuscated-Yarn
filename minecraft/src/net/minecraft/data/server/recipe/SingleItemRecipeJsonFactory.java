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
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SingleItemRecipeJsonFactory {
	private final Item output;
	private final Ingredient input;
	private final int count;
	private final Advancement.Task builder = Advancement.Task.create();
	private String group;
	private final RecipeSerializer<?> serializer;

	public SingleItemRecipeJsonFactory(RecipeSerializer<?> recipeSerializer, Ingredient ingredient, ItemConvertible itemConvertible, int i) {
		this.serializer = recipeSerializer;
		this.output = itemConvertible.asItem();
		this.input = ingredient;
		this.count = i;
	}

	public static SingleItemRecipeJsonFactory create(Ingredient ingredient, ItemConvertible itemConvertible) {
		return new SingleItemRecipeJsonFactory(RecipeSerializer.field_17640, ingredient, itemConvertible, 1);
	}

	public static SingleItemRecipeJsonFactory create(Ingredient ingredient, ItemConvertible itemConvertible, int i) {
		return new SingleItemRecipeJsonFactory(RecipeSerializer.field_17640, ingredient, itemConvertible, i);
	}

	public SingleItemRecipeJsonFactory create(String string, CriterionConditions criterionConditions) {
		this.builder.criterion(string, criterionConditions);
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.output);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Single Item Recipe " + string + " should remove its 'save' argument");
		} else {
			this.offerTo(consumer, new Identifier(string));
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
			new SingleItemRecipeJsonFactory.SingleItemRecipeJsonProvider(
				identifier,
				this.serializer,
				this.group == null ? "" : this.group,
				this.input,
				this.output,
				this.count,
				this.builder,
				new Identifier(identifier.getNamespace(), "recipes/" + this.output.getGroup().getName() + "/" + identifier.getPath())
			)
		);
	}

	private void validate(Identifier identifier) {
		if (this.builder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class SingleItemRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final String group;
		private final Ingredient input;
		private final Item output;
		private final int count;
		private final Advancement.Task builder;
		private final Identifier advancementId;
		private final RecipeSerializer<?> serializer;

		public SingleItemRecipeJsonProvider(
			Identifier identifier,
			RecipeSerializer<?> recipeSerializer,
			String string,
			Ingredient ingredient,
			Item item,
			int i,
			Advancement.Task task,
			Identifier identifier2
		) {
			this.recipeId = identifier;
			this.serializer = recipeSerializer;
			this.group = string;
			this.input = ingredient;
			this.output = item;
			this.count = i;
			this.builder = task;
			this.advancementId = identifier2;
		}

		@Override
		public void serialize(JsonObject jsonObject) {
			if (!this.group.isEmpty()) {
				jsonObject.addProperty("group", this.group);
			}

			jsonObject.add("ingredient", this.input.toJson());
			jsonObject.addProperty("result", Registry.ITEM.getId(this.output).toString());
			jsonObject.addProperty("count", this.count);
		}

		@Override
		public Identifier getRecipeId() {
			return this.recipeId;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return this.serializer;
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
