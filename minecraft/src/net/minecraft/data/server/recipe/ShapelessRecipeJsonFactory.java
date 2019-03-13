package net.minecraft.data.server.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
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
	private final SimpleAdvancement.Builder field_11393 = SimpleAdvancement.Builder.create();
	private String group;

	public ShapelessRecipeJsonFactory(ItemProvider itemProvider, int i) {
		this.output = itemProvider.getItem();
		this.outputCount = i;
	}

	public static ShapelessRecipeJsonFactory create(ItemProvider itemProvider) {
		return new ShapelessRecipeJsonFactory(itemProvider, 1);
	}

	public static ShapelessRecipeJsonFactory create(ItemProvider itemProvider, int i) {
		return new ShapelessRecipeJsonFactory(itemProvider, i);
	}

	public ShapelessRecipeJsonFactory method_10446(Tag<Item> tag) {
		return this.input(Ingredient.method_8106(tag));
	}

	public ShapelessRecipeJsonFactory input(ItemProvider itemProvider) {
		return this.input(itemProvider, 1);
	}

	public ShapelessRecipeJsonFactory input(ItemProvider itemProvider, int i) {
		for (int j = 0; j < i; j++) {
			this.input(Ingredient.method_8091(itemProvider));
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

	public ShapelessRecipeJsonFactory method_10442(String string, CriterionConditions criterionConditions) {
		this.field_11393.method_709(string, criterionConditions);
		return this;
	}

	public ShapelessRecipeJsonFactory group(String string) {
		this.group = string;
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer) {
		this.method_10443(consumer, Registry.ITEM.method_10221(this.output));
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
		Identifier identifier = Registry.ITEM.method_10221(this.output);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Shapeless Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_10443(consumer, new Identifier(string));
		}
	}

	public void method_10443(Consumer<RecipeJsonProvider> consumer, Identifier identifier) {
		this.method_10445(identifier);
		this.field_11393
			.method_708(new Identifier("recipes/root"))
			.method_709("has_the_recipe", new RecipeUnlockedCriterion.Conditions(identifier))
			.method_703(AdvancementRewards.Builder.recipe(identifier))
			.criteriaMerger(CriteriaMerger.OR);
		consumer.accept(
			new ShapelessRecipeJsonFactory.ShapelessRecipeJsonProvider(
				identifier,
				this.output,
				this.outputCount,
				this.group == null ? "" : this.group,
				this.inputs,
				this.field_11393,
				new Identifier(identifier.getNamespace(), "recipes/" + this.output.getItemGroup().getName() + "/" + identifier.getPath())
			)
		);
	}

	private void method_10445(Identifier identifier) {
		if (this.field_11393.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class ShapelessRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier field_11402;
		private final Item output;
		private final int count;
		private final String group;
		private final List<Ingredient> inputs;
		private final SimpleAdvancement.Builder field_11401;
		private final Identifier field_11405;

		public ShapelessRecipeJsonProvider(
			Identifier identifier, Item item, int i, String string, List<Ingredient> list, SimpleAdvancement.Builder builder, Identifier identifier2
		) {
			this.field_11402 = identifier;
			this.output = item;
			this.count = i;
			this.group = string;
			this.inputs = list;
			this.field_11401 = builder;
			this.field_11405 = identifier2;
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
			jsonObject2.addProperty("item", Registry.ITEM.method_10221(this.output).toString());
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
		public Identifier method_10417() {
			return this.field_11402;
		}

		@Nullable
		@Override
		public JsonObject toAdvancementJson() {
			return this.field_11401.toJson();
		}

		@Nullable
		@Override
		public Identifier method_10418() {
			return this.field_11405;
		}
	}
}
