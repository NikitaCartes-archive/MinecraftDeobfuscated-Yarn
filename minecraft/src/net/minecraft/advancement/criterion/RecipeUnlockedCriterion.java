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
	private static final Identifier field_9738 = new Identifier("recipe_unlocked");
	private final Map<PlayerAdvancementTracker, RecipeUnlockedCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, RecipeUnlockedCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9738;
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

		handler.method_9109(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions> conditionsContainer
	) {
		RecipeUnlockedCriterion.Handler handler = (RecipeUnlockedCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_9111(conditionsContainer);
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

	public void method_9107(ServerPlayerEntity serverPlayerEntity, Recipe<?> recipe) {
		RecipeUnlockedCriterion.Handler handler = (RecipeUnlockedCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(recipe);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Identifier field_9742;

		public Conditions(Identifier identifier) {
			super(RecipeUnlockedCriterion.field_9738);
			this.field_9742 = identifier;
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("recipe", this.field_9742.toString());
			return jsonObject;
		}

		public boolean matches(Recipe<?> recipe) {
			return this.field_9742.equals(recipe.method_8114());
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9741;
		private final Set<Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9741 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_9109(Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_9111(Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(Recipe<?> recipe) {
			List<Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().matches(recipe)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<RecipeUnlockedCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9741);
				}
			}
		}
	}
}
