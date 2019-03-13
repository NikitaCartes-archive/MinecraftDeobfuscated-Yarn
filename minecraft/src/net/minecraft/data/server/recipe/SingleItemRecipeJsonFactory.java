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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SingleItemRecipeJsonFactory {
	private final Item output;
	private final Ingredient input;
	private final int count;
	private final SimpleAdvancement.Builder field_17693 = SimpleAdvancement.Builder.create();
	private String group;
	private final RecipeSerializer<?> serializer;

	public SingleItemRecipeJsonFactory(RecipeSerializer<?> recipeSerializer, Ingredient ingredient, ItemProvider itemProvider, int i) {
		this.serializer = recipeSerializer;
		this.output = itemProvider.getItem();
		this.input = ingredient;
		this.count = i;
	}

	public static SingleItemRecipeJsonFactory create(Ingredient ingredient, ItemProvider itemProvider) {
		return new SingleItemRecipeJsonFactory(RecipeSerializer.field_17640, ingredient, itemProvider, 1);
	}

	public static SingleItemRecipeJsonFactory create(Ingredient ingredient, ItemProvider itemProvider, int i) {
		return new SingleItemRecipeJsonFactory(RecipeSerializer.field_17640, ingredient, itemProvider, i);
	}

	public SingleItemRecipeJsonFactory method_17970(String string, CriterionConditions criterionConditions) {
		this.field_17693.method_709(string, criterionConditions);
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
		Identifier identifier = Registry.ITEM.method_10221(this.output);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Single Item Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_17972(consumer, new Identifier(string));
		}
	}

	public void method_17972(Consumer<RecipeJsonProvider> consumer, Identifier identifier) {
		this.method_17973(identifier);
		this.field_17693
			.method_708(new Identifier("recipes/root"))
			.method_709("has_the_recipe", new RecipeUnlockedCriterion.Conditions(identifier))
			.method_703(AdvancementRewards.Builder.recipe(identifier))
			.criteriaMerger(CriteriaMerger.OR);
		consumer.accept(
			new SingleItemRecipeJsonFactory.SingleItemRecipeJsonProvider(
				identifier,
				this.serializer,
				this.group == null ? "" : this.group,
				this.input,
				this.output,
				this.count,
				this.field_17693,
				new Identifier(identifier.getNamespace(), "recipes/" + this.output.getItemGroup().getName() + "/" + identifier.getPath())
			)
		);
	}

	private void method_17973(Identifier identifier) {
		if (this.field_17693.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class SingleItemRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier field_17696;
		private final String group;
		private final Ingredient input;
		private final Item output;
		private final int count;
		private final SimpleAdvancement.Builder field_17701;
		private final Identifier field_17702;
		private final RecipeSerializer<?> serializer;

		public SingleItemRecipeJsonProvider(
			Identifier identifier,
			RecipeSerializer<?> recipeSerializer,
			String string,
			Ingredient ingredient,
			Item item,
			int i,
			SimpleAdvancement.Builder builder,
			Identifier identifier2
		) {
			this.field_17696 = identifier;
			this.serializer = recipeSerializer;
			this.group = string;
			this.input = ingredient;
			this.output = item;
			this.count = i;
			this.field_17701 = builder;
			this.field_17702 = identifier2;
		}

		@Override
		public void serialize(JsonObject jsonObject) {
			if (!this.group.isEmpty()) {
				jsonObject.addProperty("group", this.group);
			}

			jsonObject.add("ingredient", this.input.toJson());
			jsonObject.addProperty("result", Registry.ITEM.method_10221(this.output).toString());
			jsonObject.addProperty("count", this.count);
		}

		@Override
		public Identifier method_10417() {
			return this.field_17696;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return this.serializer;
		}

		@Nullable
		@Override
		public JsonObject toAdvancementJson() {
			return this.field_17701.toJson();
		}

		@Nullable
		@Override
		public Identifier method_10418() {
			return this.field_17702;
		}
	}
}
