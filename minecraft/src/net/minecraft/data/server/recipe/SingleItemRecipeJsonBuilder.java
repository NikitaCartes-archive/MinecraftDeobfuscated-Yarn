package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SingleItemRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
	private final Item output;
	private final Ingredient input;
	private final int count;
	private final Advancement.Builder advancementBuilder = Advancement.Builder.create();
	@Nullable
	private String group;
	private final RecipeSerializer<?> serializer;

	public SingleItemRecipeJsonBuilder(RecipeSerializer<?> serializer, Ingredient input, ItemConvertible output, int outputCount) {
		this.serializer = serializer;
		this.output = output.asItem();
		this.input = input;
		this.count = outputCount;
	}

	public static SingleItemRecipeJsonBuilder createStonecutting(Ingredient input, ItemConvertible output) {
		return new SingleItemRecipeJsonBuilder(RecipeSerializer.STONECUTTING, input, output, 1);
	}

	public static SingleItemRecipeJsonBuilder createStonecutting(Ingredient input, ItemConvertible output, int outputCount) {
		return new SingleItemRecipeJsonBuilder(RecipeSerializer.STONECUTTING, input, output, outputCount);
	}

	public SingleItemRecipeJsonBuilder criterion(String string, CriterionConditions criterionConditions) {
		this.advancementBuilder.criterion(string, criterionConditions);
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
	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		this.advancementBuilder
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(CriterionMerger.OR);
		exporter.accept(
			new SingleItemRecipeJsonBuilder.SingleItemRecipeJsonProvider(
				recipeId,
				this.serializer,
				this.group == null ? "" : this.group,
				this.input,
				this.output,
				this.count,
				this.advancementBuilder,
				new Identifier(recipeId.getNamespace(), "recipes/" + this.output.getGroup().getName() + "/" + recipeId.getPath())
			)
		);
	}

	private void validate(Identifier recipeId) {
		if (this.advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static class SingleItemRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final String group;
		private final Ingredient input;
		private final Item output;
		private final int count;
		private final Advancement.Builder advancementBuilder;
		private final Identifier advancementId;
		private final RecipeSerializer<?> serializer;

		public SingleItemRecipeJsonProvider(
			Identifier recipeId,
			RecipeSerializer<?> serializer,
			String group,
			Ingredient input,
			Item output,
			int outputCount,
			Advancement.Builder advancementBuilder,
			Identifier advancementId
		) {
			this.recipeId = recipeId;
			this.serializer = serializer;
			this.group = group;
			this.input = input;
			this.output = output;
			this.count = outputCount;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
		}

		@Override
		public void serialize(JsonObject json) {
			if (!this.group.isEmpty()) {
				json.addProperty("group", this.group);
			}

			json.add("ingredient", this.input.toJson());
			json.addProperty("result", Registry.ITEM.getId(this.output).toString());
			json.addProperty("count", this.count);
		}

		@Override
		public Identifier getRecipeId() {
			return this.recipeId;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return this.serializer;
		}

		@Nullable
		@Override
		public JsonObject toAdvancementJson() {
			return this.advancementBuilder.toJson();
		}

		@Nullable
		@Override
		public Identifier getAdvancementId() {
			return this.advancementId;
		}
	}
}
