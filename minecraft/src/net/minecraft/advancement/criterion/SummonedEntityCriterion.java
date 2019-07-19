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
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SummonedEntityCriterion implements Criterion<SummonedEntityCriterion.Conditions> {
	private static final Identifier ID = new Identifier("summoned_entity");
	private final Map<PlayerAdvancementTracker, SummonedEntityCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, SummonedEntityCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainer) {
		SummonedEntityCriterion.Handler handler = (SummonedEntityCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new SummonedEntityCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainer) {
		SummonedEntityCriterion.Handler handler = (SummonedEntityCriterion.Handler)this.handlers.get(manager);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(manager);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker tracker) {
		this.handlers.remove(tracker);
	}

	public SummonedEntityCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
		return new SummonedEntityCriterion.Conditions(entityPredicate);
	}

	public void trigger(ServerPlayerEntity player, Entity entity) {
		SummonedEntityCriterion.Handler handler = (SummonedEntityCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(player, entity);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate entity;

		public Conditions(EntityPredicate entityPredicate) {
			super(SummonedEntityCriterion.ID);
			this.entity = entityPredicate;
		}

		public static SummonedEntityCriterion.Conditions create(EntityPredicate.Builder builder) {
			return new SummonedEntityCriterion.Conditions(builder.build());
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, Entity entity) {
			return this.entity.test(serverPlayerEntity, entity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.entity.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerPlayerEntity serverPlayerEntity, Entity entity) {
			List<Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(serverPlayerEntity, entity)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
