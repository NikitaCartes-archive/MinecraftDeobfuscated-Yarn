package net.minecraft.data.server.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ShapelessRecipeJsonBuilder extends RecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RecipeCategory category;
	private final Item output;
	private final int count;
	private final List<Ingredient> inputs = Lists.<Ingredient>newArrayList();
	private final Map<String, AdvancementCriterion<?>> advancementBuilder = new LinkedHashMap();
	@Nullable
	private String group;

	public ShapelessRecipeJsonBuilder(RecipeCategory category, ItemConvertible output, int count) {
		this.category = category;
		this.output = output.asItem();
		this.count = count;
	}

	public static ShapelessRecipeJsonBuilder create(RecipeCategory category, ItemConvertible output) {
		return new ShapelessRecipeJsonBuilder(category, output, 1);
	}

	public static ShapelessRecipeJsonBuilder create(RecipeCategory category, ItemConvertible output, int count) {
		return new ShapelessRecipeJsonBuilder(category, output, count);
	}

	public ShapelessRecipeJsonBuilder input(TagKey<Item> tag) {
		return this.input(Ingredient.fromTag(tag));
	}

	public ShapelessRecipeJsonBuilder input(ItemConvertible itemProvider) {
		return this.input(itemProvider, 1);
	}

	public ShapelessRecipeJsonBuilder input(ItemConvertible itemProvider, int size) {
		for (int i = 0; i < size; i++) {
			this.input(Ingredient.ofItems(itemProvider));
		}

		return this;
	}

	public ShapelessRecipeJsonBuilder input(Ingredient ingredient) {
		return this.input(ingredient, 1);
	}

	public ShapelessRecipeJsonBuilder input(Ingredient ingredient, int size) {
		for (int i = 0; i < size; i++) {
			this.inputs.add(ingredient);
		}

		return this;
	}

	public ShapelessRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
		this.advancementBuilder.put(string, advancementCriterion);
		return this;
	}

	public ShapelessRecipeJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	@Override
	public Item getOutputItem() {
		return this.output;
	}

	@Override
	public void offerTo(RecipeExporter exporter, Identifier recipeId) {
		this.validate(recipeId);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		this.advancementBuilder.forEach(builder::criterion);
		exporter.accept(
			new ShapelessRecipeJsonBuilder.ShapelessRecipeJsonProvider(
				recipeId,
				this.output,
				this.count,
				this.group == null ? "" : this.group,
				getCraftingCategory(this.category),
				this.inputs,
				builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/"))
			)
		);
	}

	private void validate(Identifier recipeId) {
		if (this.advancementBuilder.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static class ShapelessRecipeJsonProvider extends RecipeJsonBuilder.CraftingRecipeJsonProvider {
		private final Identifier recipeId;
		private final Item output;
		private final int count;
		private final String group;
		private final List<Ingredient> inputs;
		private final AdvancementEntry advancementBuilder;

		public ShapelessRecipeJsonProvider(
			Identifier recipeId,
			Item output,
			int outputCount,
			String group,
			CraftingRecipeCategory craftingCategory,
			List<Ingredient> inputs,
			AdvancementEntry advancementBuilder
		) {
			super(craftingCategory);
			this.recipeId = recipeId;
			this.output = output;
			this.count = outputCount;
			this.group = group;
			this.inputs = inputs;
			this.advancementBuilder = advancementBuilder;
		}

		@Override
		public void serialize(JsonObject json) {
			super.serialize(json);
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			JsonArray jsonArray = new JsonArray();

			for (Ingredient ingredient : this.inputs) {
				jsonArray.add(ingredient.toJson(false));
			}

			json.add("ingredients", jsonArray);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registries.ITEM.getId(this.output).toString());
			if (this.count > 1) {
				jsonObject.addProperty("count", this.count);
			}

			json.add("result", jsonObject);
		}

		@Override
		public RecipeSerializer<?> serializer() {
			return RecipeSerializer.SHAPELESS;
		}

		@Override
		public Identifier id() {
			return this.recipeId;
		}

		@Override
		public AdvancementEntry advancement() {
			return this.advancementBuilder;
		}
	}
}
