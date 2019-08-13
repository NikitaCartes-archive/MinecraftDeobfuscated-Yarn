package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class RecipeUnlockedCriterion implements Criterion<RecipeUnlockedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("recipe_unlocked");
	private final Map<PlayerAdvancementTracker, RecipeUnlockedCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, RecipeUnlockedCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions> conditionsContainer
	) {
		RecipeUnlockedCriterion.Handler handler = (RecipeUnlockedCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new RecipeUnlockedCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions> conditionsContainer
	) {
		RecipeUnlockedCriterion.Handler handler = (RecipeUnlockedCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public RecipeUnlockedCriterion.Conditions method_9106(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "recipe"));
		return new RecipeUnlockedCriterion.Conditions(identifier);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, Recipe<?> recipe) {
		RecipeUnlockedCriterion.Handler handler = (RecipeUnlockedCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(recipe);
		}
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

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(Recipe<?> recipe) {
			List<Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(recipe)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
