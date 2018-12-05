package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemContainer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2447 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Item field_11380;
	private final int field_11378;
	private final List<String> field_11377 = Lists.<String>newArrayList();
	private final Map<Character, Ingredient> field_11376 = Maps.<Character, Ingredient>newLinkedHashMap();
	private final SimpleAdvancement.Builder field_11379 = SimpleAdvancement.Builder.create();
	private String field_11381;

	public class_2447(ItemContainer itemContainer, int i) {
		this.field_11380 = itemContainer.getItem();
		this.field_11378 = i;
	}

	public static class_2447 method_10437(ItemContainer itemContainer) {
		return method_10436(itemContainer, 1);
	}

	public static class_2447 method_10436(ItemContainer itemContainer, int i) {
		return new class_2447(itemContainer, i);
	}

	public class_2447 method_10433(Character character, Tag<Item> tag) {
		return this.method_10428(character, Ingredient.fromTag(tag));
	}

	public class_2447 method_10434(Character character, ItemContainer itemContainer) {
		return this.method_10428(character, Ingredient.ofItems(itemContainer));
	}

	public class_2447 method_10428(Character character, Ingredient ingredient) {
		if (this.field_11376.containsKey(character)) {
			throw new IllegalArgumentException("Symbol '" + character + "' is already defined!");
		} else if (character == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.field_11376.put(character, ingredient);
			return this;
		}
	}

	public class_2447 method_10439(String string) {
		if (!this.field_11377.isEmpty() && string.length() != ((String)this.field_11377.get(0)).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.field_11377.add(string);
			return this;
		}
	}

	public class_2447 method_10429(String string, CriterionConditions criterionConditions) {
		this.field_11379.criterion(string, criterionConditions);
		return this;
	}

	public class_2447 method_10435(String string) {
		this.field_11381 = string;
		return this;
	}

	public void method_10431(Consumer<class_2444> consumer) {
		this.method_10430(consumer, Registry.ITEM.getId(this.field_11380));
	}

	public void method_10438(Consumer<class_2444> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.field_11380);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Shaped Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_10430(consumer, new Identifier(string));
		}
	}

	public void method_10430(Consumer<class_2444> consumer, Identifier identifier) {
		this.method_10432(identifier);
		this.field_11379
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(identifier))
			.method_703(AdvancementRewards.Builder.method_753(identifier))
			.method_704(class_193.OR);
		consumer.accept(
			new class_2447.class_2448(
				identifier,
				this.field_11380,
				this.field_11378,
				this.field_11381 == null ? "" : this.field_11381,
				this.field_11377,
				this.field_11376,
				this.field_11379,
				new Identifier(identifier.getNamespace(), "recipes/" + this.field_11380.getItemGroup().method_7751() + "/" + identifier.getPath())
			)
		);
	}

	private void method_10432(Identifier identifier) {
		if (this.field_11377.isEmpty()) {
			throw new IllegalStateException("No pattern is defined for shaped recipe " + identifier + "!");
		} else {
			Set<Character> set = Sets.<Character>newHashSet(this.field_11376.keySet());
			set.remove(' ');

			for (String string : this.field_11377) {
				for (int i = 0; i < string.length(); i++) {
					char c = string.charAt(i);
					if (!this.field_11376.containsKey(c) && c != ' ') {
						throw new IllegalStateException("Pattern in recipe " + identifier + " uses undefined symbol '" + c + "'");
					}

					set.remove(c);
				}
			}

			if (!set.isEmpty()) {
				throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + identifier);
			} else if (this.field_11377.size() == 1 && ((String)this.field_11377.get(0)).length() == 1) {
				throw new IllegalStateException("Shaped recipe " + identifier + " only takes in a single item - should it be a shapeless recipe instead?");
			} else if (this.field_11379.method_710().isEmpty()) {
				throw new IllegalStateException("No way of obtaining recipe " + identifier);
			}
		}
	}

	class class_2448 implements class_2444 {
		private final Identifier field_11385;
		private final Item field_11383;
		private final int field_11386;
		private final String field_11387;
		private final List<String> field_11384;
		private final Map<Character, Ingredient> field_11388;
		private final SimpleAdvancement.Builder field_11389;
		private final Identifier field_11390;

		public class_2448(
			Identifier identifier,
			Item item,
			int i,
			String string,
			List<String> list,
			Map<Character, Ingredient> map,
			SimpleAdvancement.Builder builder,
			Identifier identifier2
		) {
			this.field_11385 = identifier;
			this.field_11383 = item;
			this.field_11386 = i;
			this.field_11387 = string;
			this.field_11384 = list;
			this.field_11388 = map;
			this.field_11389 = builder;
			this.field_11390 = identifier2;
		}

		@Override
		public JsonObject method_10416() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", "crafting_shaped");
			if (!this.field_11387.isEmpty()) {
				jsonObject.addProperty("group", this.field_11387);
			}

			JsonArray jsonArray = new JsonArray();

			for (String string : this.field_11384) {
				jsonArray.add(string);
			}

			jsonObject.add("pattern", jsonArray);
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<Character, Ingredient> entry : this.field_11388.entrySet()) {
				jsonObject2.add(String.valueOf(entry.getKey()), ((Ingredient)entry.getValue()).toJson());
			}

			jsonObject.add("key", jsonObject2);
			JsonObject jsonObject3 = new JsonObject();
			jsonObject3.addProperty("item", Registry.ITEM.getId(this.field_11383).toString());
			if (this.field_11386 > 1) {
				jsonObject3.addProperty("count", this.field_11386);
			}

			jsonObject.add("result", jsonObject3);
			return jsonObject;
		}

		@Override
		public Identifier method_10417() {
			return this.field_11385;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_11389.method_698();
		}

		@Nullable
		@Override
		public Identifier method_10418() {
			return this.field_11390;
		}
	}
}
