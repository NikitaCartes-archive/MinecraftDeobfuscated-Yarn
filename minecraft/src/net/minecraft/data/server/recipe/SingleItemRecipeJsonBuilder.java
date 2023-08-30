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
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class SingleItemRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RecipeCategory category;
	private final Item output;
	private final Ingredient input;
	private final int count;
	private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap();
	@Nullable
	private String group;
	private final RecipeSerializer<?> serializer;

	public SingleItemRecipeJsonBuilder(RecipeCategory category, RecipeSerializer<?> serializer, Ingredient input, ItemConvertible output, int count) {
		this.category = category;
		this.serializer = serializer;
		this.output = output.asItem();
		this.input = input;
		this.count = count;
	}

	public static SingleItemRecipeJsonBuilder createStonecutting(Ingredient input, RecipeCategory category, ItemConvertible output) {
		return new SingleItemRecipeJsonBuilder(category, RecipeSerializer.STONECUTTING, input, output, 1);
	}

	public static SingleItemRecipeJsonBuilder createStonecutting(Ingredient input, RecipeCategory category, ItemConvertible output, int count) {
		return new SingleItemRecipeJsonBuilder(category, RecipeSerializer.STONECUTTING, input, output, count);
	}

	public SingleItemRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
		this.criteria.put(string, advancementCriterion);
		return this;
	}

	public SingleItemRecipeJsonBuilder group(@Nullable String string) {
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
			new SingleItemRecipeJsonBuilder.SingleItemRecipeJsonProvider(
				recipeId,
				this.serializer,
				this.group == null ? "" : this.group,
				this.input,
				this.output,
				this.count,
				builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/"))
			)
		);
	}

	private void validate(Identifier recipeId) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static record SingleItemRecipeJsonProvider(
		Identifier id, RecipeSerializer<?> serializer, String group, Ingredient input, Item output, int count, AdvancementEntry advancement
	) implements RecipeJsonProvider {
		@Override
		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			json.add("ingredient", this.input.toJson(false));
			json.addProperty("result", Registries.ITEM.getId(this.output).toString());
			json.addProperty("count", this.count);
		}
	}
}
