package net.minecraft;

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

public class class_3981 {
	private final Item field_17690;
	private final Ingredient field_17691;
	private final int field_17692;
	private final SimpleAdvancement.Builder field_17693 = SimpleAdvancement.Builder.create();
	private String field_17694;
	private final RecipeSerializer<?> field_17695;

	public class_3981(RecipeSerializer<?> recipeSerializer, Ingredient ingredient, ItemProvider itemProvider, int i) {
		this.field_17695 = recipeSerializer;
		this.field_17690 = itemProvider.getItem();
		this.field_17691 = ingredient;
		this.field_17692 = i;
	}

	public static class_3981 method_17968(Ingredient ingredient, ItemProvider itemProvider) {
		return new class_3981(RecipeSerializer.field_17640, ingredient, itemProvider, 1);
	}

	public static class_3981 method_17969(Ingredient ingredient, ItemProvider itemProvider, int i) {
		return new class_3981(RecipeSerializer.field_17640, ingredient, itemProvider, i);
	}

	public class_3981 method_17970(String string, CriterionConditions criterionConditions) {
		this.field_17693.criterion(string, criterionConditions);
		return this;
	}

	public void method_17971(Consumer<class_2444> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.field_17690);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Single Item Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_17972(consumer, new Identifier(string));
		}
	}

	public void method_17972(Consumer<class_2444> consumer, Identifier identifier) {
		this.method_17973(identifier);
		this.field_17693
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(identifier))
			.rewards(AdvancementRewards.Builder.recipe(identifier))
			.criteriaMerger(CriteriaMerger.OR);
		consumer.accept(
			new class_3981.class_3982(
				identifier,
				this.field_17695,
				this.field_17694 == null ? "" : this.field_17694,
				this.field_17691,
				this.field_17690,
				this.field_17692,
				this.field_17693,
				new Identifier(identifier.getNamespace(), "recipes/" + this.field_17690.getItemGroup().method_7751() + "/" + identifier.getPath())
			)
		);
	}

	private void method_17973(Identifier identifier) {
		if (this.field_17693.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class class_3982 implements class_2444 {
		private final Identifier field_17696;
		private final String field_17697;
		private final Ingredient field_17698;
		private final Item field_17699;
		private final int field_17700;
		private final SimpleAdvancement.Builder field_17701;
		private final Identifier field_17702;
		private final RecipeSerializer<?> field_17703;

		public class_3982(
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
			this.field_17703 = recipeSerializer;
			this.field_17697 = string;
			this.field_17698 = ingredient;
			this.field_17699 = item;
			this.field_17700 = i;
			this.field_17701 = builder;
			this.field_17702 = identifier2;
		}

		@Override
		public void method_10416(JsonObject jsonObject) {
			if (!this.field_17697.isEmpty()) {
				jsonObject.addProperty("group", this.field_17697);
			}

			jsonObject.add("ingredient", this.field_17698.toJson());
			jsonObject.addProperty("result", Registry.ITEM.getId(this.field_17699).toString());
			jsonObject.addProperty("count", this.field_17700);
		}

		@Override
		public Identifier method_10417() {
			return this.field_17696;
		}

		@Override
		public RecipeSerializer<?> method_17800() {
			return this.field_17703;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_17701.toJson();
		}

		@Nullable
		@Override
		public Identifier method_10418() {
			return this.field_17702;
		}
	}
}
