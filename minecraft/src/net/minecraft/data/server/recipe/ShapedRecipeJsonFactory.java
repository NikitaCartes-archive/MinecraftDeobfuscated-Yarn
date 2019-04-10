package net.minecraft.data.server.recipe;

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

public class ShapedRecipeJsonFactory {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Item output;
	private final int outputCount;
	private final List<String> pattern = Lists.<String>newArrayList();
	private final Map<Character, Ingredient> inputs = Maps.<Character, Ingredient>newLinkedHashMap();
	private final SimpleAdvancement.Task builder = SimpleAdvancement.Task.create();
	private String group;

	public ShapedRecipeJsonFactory(ItemProvider itemProvider, int i) {
		this.output = itemProvider.getItem();
		this.outputCount = i;
	}

	public static ShapedRecipeJsonFactory create(ItemProvider itemProvider) {
		return create(itemProvider, 1);
	}

	public static ShapedRecipeJsonFactory create(ItemProvider itemProvider, int i) {
		return new ShapedRecipeJsonFactory(itemProvider, i);
	}

	public ShapedRecipeJsonFactory input(Character character, Tag<Item> tag) {
		return this.input(character, Ingredient.fromTag(tag));
	}

	public ShapedRecipeJsonFactory input(Character character, ItemProvider itemProvider) {
		return this.input(character, Ingredient.ofItems(itemProvider));
	}

	public ShapedRecipeJsonFactory input(Character character, Ingredient ingredient) {
		if (this.inputs.containsKey(character)) {
			throw new IllegalArgumentException("Symbol '" + character + "' is already defined!");
		} else if (character == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.inputs.put(character, ingredient);
			return this;
		}
	}

	public ShapedRecipeJsonFactory pattern(String string) {
		if (!this.pattern.isEmpty() && string.length() != ((String)this.pattern.get(0)).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.pattern.add(string);
			return this;
		}
	}

	public ShapedRecipeJsonFactory criterion(String string, CriterionConditions criterionConditions) {
		this.builder.criterion(string, criterionConditions);
		return this;
	}

	public ShapedRecipeJsonFactory group(String string) {
		this.group = string;
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer) {
		this.offerTo(consumer, Registry.ITEM.getId(this.output));
	}

	public void offerTo(Consumer<RecipeJsonProvider> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.output);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Shaped Recipe " + string + " should remove its 'save' argument");
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
			new ShapedRecipeJsonFactory.ShapedRecipeJsonProvider(
				identifier,
				this.output,
				this.outputCount,
				this.group == null ? "" : this.group,
				this.pattern,
				this.inputs,
				this.builder,
				new Identifier(identifier.getNamespace(), "recipes/" + this.output.getItemGroup().getName() + "/" + identifier.getPath())
			)
		);
	}

	private void validate(Identifier identifier) {
		if (this.pattern.isEmpty()) {
			throw new IllegalStateException("No pattern is defined for shaped recipe " + identifier + "!");
		} else {
			Set<Character> set = Sets.<Character>newHashSet(this.inputs.keySet());
			set.remove(' ');

			for (String string : this.pattern) {
				for (int i = 0; i < string.length(); i++) {
					char c = string.charAt(i);
					if (!this.inputs.containsKey(c) && c != ' ') {
						throw new IllegalStateException("Pattern in recipe " + identifier + " uses undefined symbol '" + c + "'");
					}

					set.remove(c);
				}
			}

			if (!set.isEmpty()) {
				throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + identifier);
			} else if (this.pattern.size() == 1 && ((String)this.pattern.get(0)).length() == 1) {
				throw new IllegalStateException("Shaped recipe " + identifier + " only takes in a single item - should it be a shapeless recipe instead?");
			} else if (this.builder.getCriteria().isEmpty()) {
				throw new IllegalStateException("No way of obtaining recipe " + identifier);
			}
		}
	}

	class ShapedRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final Item output;
		private final int resultCount;
		private final String group;
		private final List<String> pattern;
		private final Map<Character, Ingredient> inputs;
		private final SimpleAdvancement.Task builder;
		private final Identifier advancementId;

		public ShapedRecipeJsonProvider(
			Identifier identifier,
			Item item,
			int i,
			String string,
			List<String> list,
			Map<Character, Ingredient> map,
			SimpleAdvancement.Task task,
			Identifier identifier2
		) {
			this.recipeId = identifier;
			this.output = item;
			this.resultCount = i;
			this.group = string;
			this.pattern = list;
			this.inputs = map;
			this.builder = task;
			this.advancementId = identifier2;
		}

		@Override
		public void serialize(JsonObject jsonObject) {
			if (!this.group.isEmpty()) {
				jsonObject.addProperty("group", this.group);
			}

			JsonArray jsonArray = new JsonArray();

			for (String string : this.pattern) {
				jsonArray.add(string);
			}

			jsonObject.add("pattern", jsonArray);
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<Character, Ingredient> entry : this.inputs.entrySet()) {
				jsonObject2.add(String.valueOf(entry.getKey()), ((Ingredient)entry.getValue()).toJson());
			}

			jsonObject.add("key", jsonObject2);
			JsonObject jsonObject3 = new JsonObject();
			jsonObject3.addProperty("item", Registry.ITEM.getId(this.output).toString());
			if (this.resultCount > 1) {
				jsonObject3.addProperty("count", this.resultCount);
			}

			jsonObject.add("result", jsonObject3);
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return RecipeSerializer.SHAPED;
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
