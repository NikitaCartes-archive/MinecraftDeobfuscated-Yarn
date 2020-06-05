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

public class SmithingRecipeJsonFactory {
	private final Ingredient base;
	private final Ingredient addition;
	private final Item result;
	private final Advancement.Task builder = Advancement.Task.create();
	private final RecipeSerializer<?> serializer;

	public SmithingRecipeJsonFactory(RecipeSerializer<?> serializer, Ingredient base, Ingredient addition, Item result) {
		this.serializer = serializer;
		this.base = base;
		this.addition = addition;
		this.result = result;
	}

	public static SmithingRecipeJsonFactory create(Ingredient base, Ingredient addition, Item result) {
		return new SmithingRecipeJsonFactory(RecipeSerializer.SMITHING, base, addition, result);
	}

	public SmithingRecipeJsonFactory criterion(String criterionName, CriterionConditions conditions) {
		this.builder.criterion(criterionName, conditions);
		return this;
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipeId) {
		this.offerTo(exporter, new Identifier(recipeId));
	}

	public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
		this.validate(recipeId);
		this.builder
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(CriterionMerger.OR);
		exporter.accept(
			new SmithingRecipeJsonFactory.SmithingRecipeJsonProvider(
				recipeId,
				this.serializer,
				this.base,
				this.addition,
				this.result,
				this.builder,
				new Identifier(recipeId.getNamespace(), "recipes/" + this.result.getGroup().getName() + "/" + recipeId.getPath())
			)
		);
	}

	private void validate(Identifier recipeId) {
		if (this.builder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static class SmithingRecipeJsonProvider implements RecipeJsonProvider {
		private final Identifier recipeId;
		private final Ingredient base;
		private final Ingredient addition;
		private final Item result;
		private final Advancement.Task builder;
		private final Identifier advancementId;
		private final RecipeSerializer<?> serializer;

		public SmithingRecipeJsonProvider(
			Identifier recipeId, RecipeSerializer<?> serializer, Ingredient base, Ingredient addition, Item result, Advancement.Task builder, Identifier advancementId
		) {
			this.recipeId = recipeId;
			this.serializer = serializer;
			this.base = base;
			this.addition = addition;
			this.result = result;
			this.builder = builder;
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
			return this.builder.toJson();
		}

		@Nullable
		@Override
		public Identifier getAdvancementId() {
			return this.advancementId;
		}
	}
}
