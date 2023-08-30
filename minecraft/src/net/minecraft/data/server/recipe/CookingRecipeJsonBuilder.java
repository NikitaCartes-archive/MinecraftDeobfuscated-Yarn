package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class CookingRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RecipeCategory category;
	private final CookingRecipeCategory cookingCategory;
	private final Item output;
	private final Ingredient input;
	private final float experience;
	private final int cookingTime;
	private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap();
	@Nullable
	private String group;
	private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

	private CookingRecipeJsonBuilder(
		RecipeCategory category,
		CookingRecipeCategory cookingCategory,
		ItemConvertible output,
		Ingredient input,
		float experience,
		int cookingTime,
		RecipeSerializer<? extends AbstractCookingRecipe> serializer
	) {
		this.category = category;
		this.cookingCategory = cookingCategory;
		this.output = output.asItem();
		this.input = input;
		this.experience = experience;
		this.cookingTime = cookingTime;
		this.serializer = serializer;
	}

	public static CookingRecipeJsonBuilder create(
		Ingredient input,
		RecipeCategory category,
		ItemConvertible output,
		float experience,
		int cookingTime,
		RecipeSerializer<? extends AbstractCookingRecipe> serializer
	) {
		return new CookingRecipeJsonBuilder(category, getCookingRecipeCategory(serializer, output), output, input, experience, cookingTime, serializer);
	}

	public static CookingRecipeJsonBuilder createCampfireCooking(
		Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime
	) {
		return new CookingRecipeJsonBuilder(category, CookingRecipeCategory.FOOD, output, input, experience, cookingTime, RecipeSerializer.CAMPFIRE_COOKING);
	}

	public static CookingRecipeJsonBuilder createBlasting(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime) {
		return new CookingRecipeJsonBuilder(category, getBlastingRecipeCategory(output), output, input, experience, cookingTime, RecipeSerializer.BLASTING);
	}

	public static CookingRecipeJsonBuilder createSmelting(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime) {
		return new CookingRecipeJsonBuilder(category, getSmeltingRecipeCategory(output), output, input, experience, cookingTime, RecipeSerializer.SMELTING);
	}

	public static CookingRecipeJsonBuilder createSmoking(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime) {
		return new CookingRecipeJsonBuilder(category, CookingRecipeCategory.FOOD, output, input, experience, cookingTime, RecipeSerializer.SMOKING);
	}

	public CookingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
		this.criteria.put(string, advancementCriterion);
		return this;
	}

	public CookingRecipeJsonBuilder group(@Nullable String string) {
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
		this.criteria.forEach(builder::criterion);
		exporter.accept(
			new CookingRecipeJsonBuilder.CookingRecipeJsonProvider(
				recipeId,
				this.group == null ? "" : this.group,
				this.cookingCategory,
				this.input,
				this.output,
				this.experience,
				this.cookingTime,
				builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")),
				this.serializer
			)
		);
	}

	private static CookingRecipeCategory getSmeltingRecipeCategory(ItemConvertible output) {
		if (output.asItem().isFood()) {
			return CookingRecipeCategory.FOOD;
		} else {
			return output.asItem() instanceof BlockItem ? CookingRecipeCategory.BLOCKS : CookingRecipeCategory.MISC;
		}
	}

	private static CookingRecipeCategory getBlastingRecipeCategory(ItemConvertible output) {
		return output.asItem() instanceof BlockItem ? CookingRecipeCategory.BLOCKS : CookingRecipeCategory.MISC;
	}

	private static CookingRecipeCategory getCookingRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> serializer, ItemConvertible output) {
		if (serializer == RecipeSerializer.SMELTING) {
			return getSmeltingRecipeCategory(output);
		} else if (serializer == RecipeSerializer.BLASTING) {
			return getBlastingRecipeCategory(output);
		} else if (serializer != RecipeSerializer.SMOKING && serializer != RecipeSerializer.CAMPFIRE_COOKING) {
			throw new IllegalStateException("Unknown cooking recipe type");
		} else {
			return CookingRecipeCategory.FOOD;
		}
	}

	private void validate(Identifier recipeId) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	static record CookingRecipeJsonProvider(
		Identifier id,
		String group,
		CookingRecipeCategory category,
		Ingredient input,
		Item result,
		float experience,
		int cookingTime,
		AdvancementEntry advancement,
		RecipeSerializer<? extends AbstractCookingRecipe> serializer
	) implements RecipeJsonProvider {
		@Override
		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			json.addProperty("category", this.category.asString());
			json.add("ingredient", this.input.toJson(false));
			json.addProperty("result", Registries.ITEM.getId(this.result).toString());
			json.addProperty("experience", this.experience);
			json.addProperty("cookingtime", this.cookingTime);
		}
	}
}
