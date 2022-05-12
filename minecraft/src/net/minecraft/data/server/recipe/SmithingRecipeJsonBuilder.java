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
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SmithingRecipeJsonBuilder {
	private final Ingredient base;
	private final Ingredient addition;
	private final Item result;
	private final Advancement.Builder advancementBuilder = Advancement.Builder.create();
	private final RecipeSerializer<?> serializer;

	public SmithingRecipeJsonBuilder(RecipeSerializer<?> serializer, Ingredient base, Ingredient addition, Item result) {
		this.serializer = serializer;
		this.base = base;
		this.addition = addition;
		this.result = result;
	}

	public static SmithingRecipeJsonBuilder create(Ingredient base, Ingredient addition, Item result) {
		return new SmithingRecipeJsonBuilder(RecipeSerializer.SMITHING, base, addition, result);
	}

	public SmithingRecipeJsonBuilder criterion(String criterionName, CriterionConditions conditions) {
		this.advancementBuilder.criterion(criterionName, conditions);
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipeId) {
		this.offerTo(exporter, new Identifier(recipeId));
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		this.advancementBuilder
			.parent(CraftingRecipeJsonBuilder.field_39377)
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(CriterionMerger.OR);
		exporter.accept(
			new SmithingRecipeJsonBuilder.SmithingRecipeJsonProvider(
				recipeId,
				this.serializer,
				this.base,
				this.addition,
				this.result,
				this.advancementBuilder,
				new Identifier(recipeId.getNamespace(), "recipes/" + this.result.getGroup().getName() + "/" + recipeId.getPath())
			)
		);
	}

	private void validate(Identifier recipeId) {
		if (this.advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static class SmithingRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final Ingredient base;
		private final Ingredient addition;
		private final Item result;
		private final Advancement.Builder advancementBuilder;
		private final Identifier advancementId;
		private final RecipeSerializer<?> serializer;

		public SmithingRecipeJsonProvider(
			Identifier recipeId,
			RecipeSerializer<?> serializer,
			Ingredient base,
			Ingredient addition,
			Item result,
			Advancement.Builder advancementBuilder,
			Identifier advancementId
		) {
			this.recipeId = recipeId;
			this.serializer = serializer;
			this.base = base;
			this.addition = addition;
			this.result = result;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
		}

		@Override
		public void serialize(JsonObject json) {
			json.add("base", this.base.toJson());
			json.add("addition", this.addition.toJson());
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getId(this.result).toString());
			json.add("result", jsonObject);
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
