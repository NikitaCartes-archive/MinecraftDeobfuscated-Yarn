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
	private static final Identifier field_9748 = new Identifier("summoned_entity");
	private final Map<PlayerAdvancementTracker, SummonedEntityCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, SummonedEntityCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9748;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainer
	) {
		SummonedEntityCriterion.Handler handler = (SummonedEntityCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new SummonedEntityCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_9125(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainer
	) {
		SummonedEntityCriterion.Handler handler = (SummonedEntityCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_9128(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public SummonedEntityCriterion.Conditions method_9123(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("entity"));
		return new SummonedEntityCriterion.Conditions(entityPredicate);
	}

	public void method_9124(ServerPlayerEntity serverPlayerEntity, Entity entity) {
		SummonedEntityCriterion.Handler handler = (SummonedEntityCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_9127(serverPlayerEntity, entity);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate entity;

		public Conditions(EntityPredicate entityPredicate) {
			super(SummonedEntityCriterion.field_9748);
			this.entity = entityPredicate;
		}

		public static SummonedEntityCriterion.Conditions method_9129(EntityPredicate.Builder builder) {
			return new SummonedEntityCriterion.Conditions(builder.build());
		}

		public boolean method_9130(ServerPlayerEntity serverPlayerEntity, Entity entity) {
			return this.entity.method_8914(serverPlayerEntity, entity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.entity.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9751;
		private final Set<Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9751 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_9125(Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_9128(Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_9127(ServerPlayerEntity serverPlayerEntity, Entity entity) {
			List<Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_9130(serverPlayerEntity, entity)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<SummonedEntityCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9751);
				}
			}
		}
	}
}
