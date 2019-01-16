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
import net.minecraft.entity.LivingEntity;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EffectsChangedCriterion implements Criterion<EffectsChangedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("effects_changed");
	private final Map<PlayerAdvancementTracker, EffectsChangedCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, EffectsChangedCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<EffectsChangedCriterion.Conditions> conditionsContainer
	) {
		EffectsChangedCriterion.Handler handler = (EffectsChangedCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new EffectsChangedCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<EffectsChangedCriterion.Conditions> conditionsContainer
	) {
		EffectsChangedCriterion.Handler handler = (EffectsChangedCriterion.Handler)this.handlers.get(playerAdvancementTracker);
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

	public EffectsChangedCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityEffectPredicate entityEffectPredicate = EntityEffectPredicate.deserialize(jsonObject.get("effects"));
		return new EffectsChangedCriterion.Conditions(entityEffectPredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity) {
		EffectsChangedCriterion.Handler handler = (EffectsChangedCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(serverPlayerEntity);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityEffectPredicate effects;

		public Conditions(EntityEffectPredicate entityEffectPredicate) {
			super(EffectsChangedCriterion.ID);
			this.effects = entityEffectPredicate;
		}

		public static EffectsChangedCriterion.Conditions method_8869(EntityEffectPredicate entityEffectPredicate) {
			return new EffectsChangedCriterion.Conditions(entityEffectPredicate);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity) {
			return this.effects.test((LivingEntity)serverPlayerEntity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("effects", this.effects.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<EffectsChangedCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<EffectsChangedCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<EffectsChangedCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<EffectsChangedCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerPlayerEntity serverPlayerEntity) {
			List<Criterion.ConditionsContainer<EffectsChangedCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<EffectsChangedCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(serverPlayerEntity)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<EffectsChangedCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<EffectsChangedCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
