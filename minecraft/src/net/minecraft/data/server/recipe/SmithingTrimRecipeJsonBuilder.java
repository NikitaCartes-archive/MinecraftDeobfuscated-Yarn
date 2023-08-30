package net.minecraft.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

public class SmithingTrimRecipeJsonBuilder {
	private final RecipeCategory category;
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;
	private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap();
	private final RecipeSerializer<?> serializer;

	public SmithingTrimRecipeJsonBuilder(RecipeSerializer<?> serializer, RecipeCategory category, Ingredient template, Ingredient base, Ingredient addition) {
		this.category = category;
		this.serializer = serializer;
		this.template = template;
		this.base = base;
		this.addition = addition;
	}

	public static SmithingTrimRecipeJsonBuilder create(Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category) {
		return new SmithingTrimRecipeJsonBuilder(RecipeSerializer.SMITHING_TRIM, category, template, base, addition);
	}

	public SmithingTrimRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
		this.criteria.put(name, criterion);
		return this;
	}

	public void offerTo(RecipeExporter exporter, Identifier recipeId) {
		this.validate(recipeId);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		this.criteria.forEach(builder::criterion);
		exporter.accept(
			new SmithingTrimRecipeJsonBuilder.SmithingTrimRecipeJsonProvider(
				recipeId, this.serializer, this.template, this.base, this.addition, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/"))
			)
		);
	}

	private void validate(Identifier recipeId) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}

	public static record SmithingTrimRecipeJsonProvider(
		Identifier id, RecipeSerializer<?> serializer, Ingredient template, Ingredient base, Ingredient addition, AdvancementEntry advancement
	) implements RecipeJsonProvider {
		@Override
		public void serialize(JsonObject json) {
			json.add("template", this.template.toJson(true));
			json.add("base", this.base.toJson(true));
			json.add("addition", this.addition.toJson(true));
		}
	}
}
