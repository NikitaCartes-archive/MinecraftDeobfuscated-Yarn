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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2454 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Item field_11417;
	private final Ingredient field_11418;
	private final float field_11414;
	private final int field_11415;
	private final SimpleAdvancement.Builder field_11416 = SimpleAdvancement.Builder.create();
	private String field_11419;

	public class_2454(Ingredient ingredient, ItemProvider itemProvider, float f, int i) {
		this.field_11417 = itemProvider.getItem();
		this.field_11418 = ingredient;
		this.field_11414 = f;
		this.field_11415 = i;
	}

	public static class_2454 method_10473(Ingredient ingredient, ItemProvider itemProvider, float f, int i) {
		return new class_2454(ingredient, itemProvider, f, i);
	}

	public class_2454 method_10469(String string, CriterionConditions criterionConditions) {
		this.field_11416.criterion(string, criterionConditions);
		return this;
	}

	public void method_10470(Consumer<class_2444> consumer) {
		this.method_10468(consumer, Registry.ITEM.getId(this.field_11417));
	}

	public void method_10472(Consumer<class_2444> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.field_11417);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Smelting Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_10468(consumer, new Identifier(string));
		}
	}

	public void method_10468(Consumer<class_2444> consumer, Identifier identifier) {
		this.method_10471(identifier);
		this.field_11416
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(identifier))
			.rewards(AdvancementRewards.Builder.recipe(identifier))
			.criteriaMerger(CriteriaMerger.OR);
		consumer.accept(
			new class_2454.class_2455(
				identifier,
				this.field_11419 == null ? "" : this.field_11419,
				this.field_11418,
				this.field_11417,
				this.field_11414,
				this.field_11415,
				this.field_11416,
				new Identifier(identifier.getNamespace(), "recipes/" + this.field_11417.getItemGroup().method_7751() + "/" + identifier.getPath())
			)
		);
	}

	private void method_10471(Identifier identifier) {
		if (this.field_11416.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class class_2455 implements class_2444 {
		private final Identifier field_11424;
		private final String group;
		private final Ingredient ingredient;
		private final Item result;
		private final float experience;
		private final int cookingTime;
		private final SimpleAdvancement.Builder field_11423;
		private final Identifier field_11427;

		public class_2455(
			Identifier identifier, String string, Ingredient ingredient, Item item, float f, int i, SimpleAdvancement.Builder builder, Identifier identifier2
		) {
			this.field_11424 = identifier;
			this.group = string;
			this.ingredient = ingredient;
			this.result = item;
			this.experience = f;
			this.cookingTime = i;
			this.field_11423 = builder;
			this.field_11427 = identifier2;
		}

		@Override
		public JsonObject method_10416() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", "smelting");
			if (!this.group.isEmpty()) {
				jsonObject.addProperty("group", this.group);
			}

			jsonObject.add("ingredient", this.ingredient.toJson());
			jsonObject.addProperty("result", Registry.ITEM.getId(this.result).toString());
			jsonObject.addProperty("experience", this.experience);
			jsonObject.addProperty("cookingtime", this.cookingTime);
			return jsonObject;
		}

		@Override
		public Identifier method_10417() {
			return this.field_11424;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_11423.toJson();
		}

		@Nullable
		@Override
		public Identifier method_10418() {
			return this.field_11427;
		}
	}
}
