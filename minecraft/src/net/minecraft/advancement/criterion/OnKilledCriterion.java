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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OnKilledCriterion implements Criterion<OnKilledCriterion.Conditions> {
	private final Map<PlayerAdvancementTracker, OnKilledCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, OnKilledCriterion.Handler>newHashMap();
	private final Identifier id;

	public OnKilledCriterion(Identifier identifier) {
		this.id = identifier;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainer) {
		OnKilledCriterion.Handler handler = (OnKilledCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new OnKilledCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainer) {
		OnKilledCriterion.Handler handler = (OnKilledCriterion.Handler)this.handlers.get(manager);
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

	public OnKilledCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new OnKilledCriterion.Conditions(
			this.id, EntityPredicate.fromJson(jsonObject.get("entity")), DamageSourcePredicate.deserialize(jsonObject.get("killing_blow"))
		);
	}

	public void trigger(ServerPlayerEntity player, Entity entity, DamageSource source) {
		OnKilledCriterion.Handler handler = (OnKilledCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(player, entity, source);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate entity;
		private final DamageSourcePredicate killingBlow;

		public Conditions(Identifier identifier, EntityPredicate entity, DamageSourcePredicate killingBlow) {
			super(identifier);
			this.entity = entity;
			this.killingBlow = killingBlow;
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(EntityPredicate.Builder builder) {
			return new OnKilledCriterion.Conditions(Criterions.PLAYER_KILLED_ENTITY.id, builder.build(), DamageSourcePredicate.EMPTY);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity() {
			return new OnKilledCriterion.Conditions(Criterions.PLAYER_KILLED_ENTITY.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(EntityPredicate.Builder builder, DamageSourcePredicate.Builder builder2) {
			return new OnKilledCriterion.Conditions(Criterions.PLAYER_KILLED_ENTITY.id, builder.build(), builder2.build());
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer() {
			return new OnKilledCriterion.Conditions(Criterions.ENTITY_KILLED_PLAYER.id, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
		}

		public boolean test(ServerPlayerEntity player, Entity entity, DamageSource killingBlow) {
			return !this.killingBlow.test(player, killingBlow) ? false : this.entity.test(player, entity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.entity.serialize());
			jsonObject.add("killing_blow", this.killingBlow.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<OnKilledCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<OnKilledCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerPlayerEntity player, Entity entity, DamageSource killingBlow) {
			List<Criterion.ConditionsContainer<OnKilledCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().test(player, entity, killingBlow)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<OnKilledCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
