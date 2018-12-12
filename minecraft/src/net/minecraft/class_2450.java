package net.minecraft;

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
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2450 {
	private static final Logger field_11397 = LogManager.getLogger();
	private final Item field_11396;
	private final int field_11395;
	private final List<Ingredient> field_11394 = Lists.<Ingredient>newArrayList();
	private final SimpleAdvancement.Builder field_11393 = SimpleAdvancement.Builder.create();
	private String field_11398;

	public class_2450(ItemProvider itemProvider, int i) {
		this.field_11396 = itemProvider.getItem();
		this.field_11395 = i;
	}

	public static class_2450 method_10447(ItemProvider itemProvider) {
		return new class_2450(itemProvider, 1);
	}

	public static class_2450 method_10448(ItemProvider itemProvider, int i) {
		return new class_2450(itemProvider, i);
	}

	public class_2450 method_10446(Tag<Item> tag) {
		return this.method_10451(Ingredient.fromTag(tag));
	}

	public class_2450 method_10454(ItemProvider itemProvider) {
		return this.method_10449(itemProvider, 1);
	}

	public class_2450 method_10449(ItemProvider itemProvider, int i) {
		for (int j = 0; j < i; j++) {
			this.method_10451(Ingredient.method_8091(itemProvider));
		}

		return this;
	}

	public class_2450 method_10451(Ingredient ingredient) {
		return this.method_10453(ingredient, 1);
	}

	public class_2450 method_10453(Ingredient ingredient, int i) {
		for (int j = 0; j < i; j++) {
			this.field_11394.add(ingredient);
		}

		return this;
	}

	public class_2450 method_10442(String string, CriterionConditions criterionConditions) {
		this.field_11393.criterion(string, criterionConditions);
		return this;
	}

	public class_2450 method_10452(String string) {
		this.field_11398 = string;
		return this;
	}

	public void method_10444(Consumer<class_2444> consumer) {
		this.method_10443(consumer, Registry.ITEM.getId(this.field_11396));
	}

	public void method_10450(Consumer<class_2444> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.field_11396);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Shapeless Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_10443(consumer, new Identifier(string));
		}
	}

	public void method_10443(Consumer<class_2444> consumer, Identifier identifier) {
		this.method_10445(identifier);
		this.field_11393
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(identifier))
			.rewards(AdvancementRewards.Builder.recipe(identifier))
			.criteriaMerger(CriteriaMerger.OR);
		consumer.accept(
			new class_2450.class_2451(
				identifier,
				this.field_11396,
				this.field_11395,
				this.field_11398 == null ? "" : this.field_11398,
				this.field_11394,
				this.field_11393,
				new Identifier(identifier.getNamespace(), "recipes/" + this.field_11396.getItemGroup().method_7751() + "/" + identifier.getPath())
			)
		);
	}

	private void method_10445(Identifier identifier) {
		if (this.field_11393.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class class_2451 implements class_2444 {
		private final Identifier field_11402;
		private final Item field_11403;
		private final int field_11400;
		private final String field_11399;
		private final List<Ingredient> field_11404;
		private final SimpleAdvancement.Builder field_11401;
		private final Identifier field_11405;

		public class_2451(Identifier identifier, Item item, int i, String string, List<Ingredient> list, SimpleAdvancement.Builder builder, Identifier identifier2) {
			this.field_11402 = identifier;
			this.field_11403 = item;
			this.field_11400 = i;
			this.field_11399 = string;
			this.field_11404 = list;
			this.field_11401 = builder;
			this.field_11405 = identifier2;
		}

		@Override
		public JsonObject method_10416() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", "crafting_shapeless");
			if (!this.field_11399.isEmpty()) {
				jsonObject.addProperty("group", this.field_11399);
			}

			JsonArray jsonArray = new JsonArray();

			for (Ingredient ingredient : this.field_11404) {
				jsonArray.add(ingredient.toJson());
			}

			jsonObject.add("ingredients", jsonArray);
			JsonObject jsonObject2 = new JsonObject();
			jsonObject2.addProperty("item", Registry.ITEM.getId(this.field_11403).toString());
			if (this.field_11400 > 1) {
				jsonObject2.addProperty("count", this.field_11400);
			}

			jsonObject.add("result", jsonObject2);
			return jsonObject;
		}

		@Override
		public Identifier method_10417() {
			return this.field_11402;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_11401.toJson();
		}

		@Nullable
		@Override
		public Identifier method_10418() {
			return this.field_11405;
		}
	}
}
