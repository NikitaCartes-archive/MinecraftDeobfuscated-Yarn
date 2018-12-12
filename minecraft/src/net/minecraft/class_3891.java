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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_3891 {
	private final Item field_17171;
	private final Ingredient field_17172;
	private final float field_17173;
	private final int field_17174;
	private final SimpleAdvancement.Builder field_17175 = SimpleAdvancement.Builder.create();
	private String field_17176;

	public class_3891(Ingredient ingredient, ItemProvider itemProvider, float f, int i) {
		this.field_17171 = itemProvider.getItem();
		this.field_17172 = ingredient;
		this.field_17173 = f;
		this.field_17174 = i;
	}

	public static class_3891 method_17176(Ingredient ingredient, ItemProvider itemProvider, float f, int i) {
		return new class_3891(ingredient, itemProvider, f, i);
	}

	public class_3891 method_17177(String string, CriterionConditions criterionConditions) {
		this.field_17175.criterion(string, criterionConditions);
		return this;
	}

	public void method_17178(Consumer<class_2444> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.field_17171);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Blasting Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_17179(consumer, new Identifier(string));
		}
	}

	public void method_17179(Consumer<class_2444> consumer, Identifier identifier) {
		this.method_17180(identifier);
		this.field_17175
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(identifier))
			.rewards(AdvancementRewards.Builder.recipe(identifier))
			.criteriaMerger(CriteriaMerger.OR);
		consumer.accept(
			new class_3891.class_3892(
				identifier,
				this.field_17176 == null ? "" : this.field_17176,
				this.field_17172,
				this.field_17171,
				this.field_17173,
				this.field_17174,
				this.field_17175,
				new Identifier(identifier.getNamespace(), "recipes/" + this.field_17171.getItemGroup().method_7751() + "/" + identifier.getPath())
			)
		);
	}

	private void method_17180(Identifier identifier) {
		if (this.field_17175.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class class_3892 implements class_2444 {
		private final Identifier field_17177;
		private final String field_17178;
		private final Ingredient field_17179;
		private final Item field_17180;
		private final float field_17181;
		private final int field_17182;
		private final SimpleAdvancement.Builder field_17183;
		private final Identifier field_17184;

		public class_3892(
			Identifier identifier, String string, Ingredient ingredient, Item item, float f, int i, SimpleAdvancement.Builder builder, Identifier identifier2
		) {
			this.field_17177 = identifier;
			this.field_17178 = string;
			this.field_17179 = ingredient;
			this.field_17180 = item;
			this.field_17181 = f;
			this.field_17182 = i;
			this.field_17183 = builder;
			this.field_17184 = identifier2;
		}

		@Override
		public JsonObject method_10416() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", "blasting");
			if (!this.field_17178.isEmpty()) {
				jsonObject.addProperty("group", this.field_17178);
			}

			jsonObject.add("ingredient", this.field_17179.toJson());
			jsonObject.addProperty("result", Registry.ITEM.getId(this.field_17180).toString());
			jsonObject.addProperty("experience", this.field_17181);
			jsonObject.addProperty("cookingtime", this.field_17182);
			return jsonObject;
		}

		@Override
		public Identifier method_10417() {
			return this.field_17177;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_17183.toJson();
		}

		@Nullable
		@Override
		public Identifier method_10418() {
			return this.field_17184;
		}
	}
}
