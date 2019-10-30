package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class RecipeUnlockedCriterion extends AbstractCriterion<RecipeUnlockedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("recipe_unlocked");

	@Override
	public Identifier getId() {
		return ID;
	}

	public RecipeUnlockedCriterion.Conditions method_9106(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "recipe"));
		return new RecipeUnlockedCriterion.Conditions(identifier);
	}

	public void trigger(ServerPlayerEntity player, Recipe<?> recipe) {
		this.test(player.getAdvancementManager(), conditions -> conditions.matches(recipe));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Identifier recipe;

		public Conditions(Identifier identifier) {
			super(RecipeUnlockedCriterion.ID);
			this.recipe = identifier;
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("recipe", this.recipe.toString());
			return jsonObject;
		}

		public boolean matches(Recipe<?> recipe) {
			return this.recipe.equals(recipe.getId());
		}
	}
}
