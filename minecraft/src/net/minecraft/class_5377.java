package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_5377 {
	private final Ingredient field_25491;
	private final Ingredient field_25492;
	private final Item field_25493;
	private final Advancement.Task field_25494 = Advancement.Task.create();
	private final RecipeSerializer<?> field_25495;

	public class_5377(RecipeSerializer<?> recipeSerializer, Ingredient ingredient, Ingredient ingredient2, Item item) {
		this.field_25495 = recipeSerializer;
		this.field_25491 = ingredient;
		this.field_25492 = ingredient2;
		this.field_25493 = item;
	}

	public static class_5377 method_29729(Ingredient ingredient, Ingredient ingredient2, Item item) {
		return new class_5377(RecipeSerializer.SMITHING, ingredient, ingredient2, item);
	}

	public class_5377 method_29730(String string, CriterionConditions criterionConditions) {
		this.field_25494.criterion(string, criterionConditions);
		return this;
	}

	public void method_29731(Consumer<RecipeJsonProvider> consumer, String string) {
		this.method_29732(consumer, new Identifier(string));
	}

	public void method_29732(Consumer<RecipeJsonProvider> consumer, Identifier identifier) {
		this.method_29733(identifier);
		this.field_25494
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(identifier))
			.rewards(AdvancementRewards.Builder.recipe(identifier))
			.criteriaMerger(CriteriaMerger.OR);
		consumer.accept(
			new class_5377.class_5378(
				identifier,
				this.field_25495,
				this.field_25491,
				this.field_25492,
				this.field_25493,
				this.field_25494,
				new Identifier(identifier.getNamespace(), "recipes/" + this.field_25493.getGroup().getName() + "/" + identifier.getPath())
			)
		);
	}

	private void method_29733(Identifier identifier) {
		if (this.field_25494.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class class_5378 implements RecipeJsonProvider {
		private final Identifier field_25496;
		private final Ingredient field_25497;
		private final Ingredient field_25498;
		private final Item field_25499;
		private final Advancement.Task field_25500;
		private final Identifier field_25501;
		private final RecipeSerializer<?> field_25502;

		public class_5378(
			Identifier identifier,
			RecipeSerializer<?> recipeSerializer,
			Ingredient ingredient,
			Ingredient ingredient2,
			Item item,
			Advancement.Task task,
			Identifier identifier2
		) {
			this.field_25496 = identifier;
			this.field_25502 = recipeSerializer;
			this.field_25497 = ingredient;
			this.field_25498 = ingredient2;
			this.field_25499 = item;
			this.field_25500 = task;
			this.field_25501 = identifier2;
		}

		@Override
		public void serialize(JsonObject json) {
			json.add("base", this.field_25497.toJson());
			json.add("addition", this.field_25498.toJson());
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getId(this.field_25499).toString());
			json.add("result", jsonObject);
		}

		@Override
		public Identifier getRecipeId() {
			return this.field_25496;
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return this.field_25502;
		}

		@Nullable
		@Override
		public JsonObject toAdvancementJson() {
			return this.field_25500.toJson();
		}

		@Nullable
		@Override
		public Identifier getAdvancementId() {
			return this.field_25501;
		}
	}
}
