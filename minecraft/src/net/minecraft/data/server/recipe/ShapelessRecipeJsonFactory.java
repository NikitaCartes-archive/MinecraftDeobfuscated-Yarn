package net.minecraft.data.server.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
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
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapelessRecipeJsonFactory {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Item output;
	private final int outputCount;
	private final List<Ingredient> inputs = Lists.<Ingredient>newArrayList();
	private final Advancement.Task builder = Advancement.Task.create();
	private String group;

	public ShapelessRecipeJsonFactory(ItemConvertible itemConvertible, int i) {
		this.output = itemConvertible.asItem();
		this.outputCount = i;
	}

	public static ShapelessRecipeJsonFactory create(ItemConvertible itemConvertible) {
		return new ShapelessRecipeJsonFactory(itemConvertible, 1);
	}

	public static ShapelessRecipeJsonFactory create(ItemConvertible itemConvertible, int i) {
		return new ShapelessRecipeJsonFactory(itemConvertible, i);
	}

	public ShapelessRecipeJsonFactory input(Tag<Item> tag) {
		return this.input(Ingredient.fromTag(tag));
	}

	public ShapelessRecipeJsonFactory input(ItemConvertible itemConvertible) {
		return this.input(itemConvertible, 1);
	}

	public ShapelessRecipeJsonFactory input(ItemConvertible itemConvertible, int i) {
		for (int j = 0; j < i; j++) {
			this.input(Ingredient.method_8091(itemConvertible));
		}

		return this;
	}

	public ShapelessRecipeJsonFactory input(Ingredient ingredient) {
		return this.input(ingredient, 1);
	}

	public ShapelessRecipeJsonFactory input(Ingredient ingredient, int i) {
		for (int j = 0; j < i; j++) {
			this.inputs.add(ingredient);
		}

		return this;
	}

	public ShapelessRecipeJsonFactory criterion(String string, CriterionConditions criterionConditions) {
		this.builder.criterion(string, criterionConditions);
		return this;
	}

	public ShapelessRecipeJsonFactory group(String string) {
		this.group = string;
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer) {
		this.offerTo(consumer, Registry.ITEM.getId(this.output));
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.output);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Shapeless Recipe " + string + " should remove its 'save' argument");
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
			new ShapelessRecipeJsonFactory.ShapelessRecipeJsonProvider(
				identifier,
				this.output,
				this.outputCount,
				this.group == null ? "" : this.group,
				this.inputs,
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

	public static class ShapelessRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final Item output;
		private final int count;
		private final String group;
		private final List<Ingredient> inputs;
		private final Advancement.Task builder;
		private final Identifier advancementId;

		public ShapelessRecipeJsonProvider(
			Identifier identifier, Item item, int i, String string, List<Ingredient> list, Advancement.Task task, Identifier identifier2
		) {
			this.recipeId = identifier;
			this.output = item;
			this.count = i;
			this.group = string;
			this.inputs = list;
			this.builder = task;
			this.advancementId = identifier2;
		}

		@Override
		public void serialize(JsonObject jsonObject) {
			if (!this.group.isEmpty()) {
				jsonObject.addProperty("group", this.group);
			}

			JsonArray jsonArray = new JsonArray();

			for (Ingredient ingredient : this.inputs) {
				jsonArray.add(ingredient.toJson());
			}

			jsonObject.add("ingredients", jsonArray);
			JsonObject jsonObject2 = new JsonObject();
			jsonObject2.addProperty("item", Registry.ITEM.getId(this.output).toString());
			if (this.count > 1) {
				jsonObject2.addProperty("count", this.count);
			}

			jsonObject.add("result", jsonObject2);
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return RecipeSerializer.SHAPELESS;
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
