package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class RecipeUnlockedCriterion extends AbstractCriterion<RecipeUnlockedCriterion.Conditions> {
	public RecipeUnlockedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "recipe"));
		return new RecipeUnlockedCriterion.Conditions(optional, identifier);
	}

	public void trigger(ServerPlayerEntity player, RecipeEntry<?> recipe) {
		this.trigger(player, conditions -> conditions.matches(recipe));
	}

	public static AdvancementCriterion<RecipeUnlockedCriterion.Conditions> create(Identifier id) {
		return Criteria.RECIPE_UNLOCKED.create(new RecipeUnlockedCriterion.Conditions(Optional.empty(), id));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Identifier recipe;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Identifier recipe) {
			super(playerPredicate);
			this.recipe = recipe;
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			jsonObject.addProperty("recipe", this.recipe.toString());
			return jsonObject;
		}

		public boolean matches(RecipeEntry<?> recipe) {
			return this.recipe.equals(recipe.id());
		}
	}
}
