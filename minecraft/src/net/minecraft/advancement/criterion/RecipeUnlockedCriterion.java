package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class RecipeUnlockedCriterion extends AbstractCriterion<RecipeUnlockedCriterion.Conditions> {
	static final Identifier ID = new Identifier("recipe_unlocked");

	@Override
	public Identifier getId() {
		return ID;
	}

	public RecipeUnlockedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "recipe"));
		return new RecipeUnlockedCriterion.Conditions(lootContextPredicate, identifier);
	}

	public void trigger(ServerPlayerEntity player, Recipe<?> recipe) {
		this.trigger(player, conditions -> conditions.matches(recipe));
	}

	public static RecipeUnlockedCriterion.Conditions create(Identifier id) {
		return new RecipeUnlockedCriterion.Conditions(LootContextPredicate.EMPTY, id);
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Identifier recipe;

		public Conditions(LootContextPredicate player, Identifier recipe) {
			super(RecipeUnlockedCriterion.ID, player);
			this.recipe = recipe;
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.addProperty("recipe", this.recipe.toString());
			return jsonObject;
		}

		public boolean matches(Recipe<?> recipe) {
			return this.recipe.equals(recipe.getId());
		}
	}
}
