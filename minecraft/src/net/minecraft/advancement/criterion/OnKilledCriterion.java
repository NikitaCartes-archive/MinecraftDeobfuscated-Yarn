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
	private final Identifier field_9661;

	public OnKilledCriterion(Identifier identifier) {
		this.field_9661 = identifier;
	}

	@Override
	public Identifier getId() {
		return this.field_9661;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainer
	) {
		OnKilledCriterion.Handler handler = (OnKilledCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new OnKilledCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_8993(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainer
	) {
		OnKilledCriterion.Handler handler = (OnKilledCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_8996(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public OnKilledCriterion.Conditions method_8989(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new OnKilledCriterion.Conditions(
			this.field_9661, EntityPredicate.deserialize(jsonObject.get("entity")), DamageSourcePredicate.deserialize(jsonObject.get("killing_blow"))
		);
	}

	public void method_8990(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
		OnKilledCriterion.Handler handler = (OnKilledCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_8994(serverPlayerEntity, entity, damageSource);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate entity;
		private final DamageSourcePredicate killingBlow;

		public Conditions(Identifier identifier, EntityPredicate entityPredicate, DamageSourcePredicate damageSourcePredicate) {
			super(identifier);
			this.entity = entityPredicate;
			this.killingBlow = damageSourcePredicate;
		}

		public static OnKilledCriterion.Conditions createKill(EntityPredicate.Builder builder) {
			return new OnKilledCriterion.Conditions(Criterions.PLAYER_KILLED_ENTITY.field_9661, builder.build(), DamageSourcePredicate.EMPTY);
		}

		public static OnKilledCriterion.Conditions method_8999() {
			return new OnKilledCriterion.Conditions(Criterions.PLAYER_KILLED_ENTITY.field_9661, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
		}

		public static OnKilledCriterion.Conditions method_9001(EntityPredicate.Builder builder, DamageSourcePredicate.Builder builder2) {
			return new OnKilledCriterion.Conditions(Criterions.PLAYER_KILLED_ENTITY.field_9661, builder.build(), builder2.build());
		}

		public static OnKilledCriterion.Conditions method_8998() {
			return new OnKilledCriterion.Conditions(Criterions.ENTITY_KILLED_PLAYER.field_9661, EntityPredicate.ANY, DamageSourcePredicate.EMPTY);
		}

		public boolean method_9000(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
			return !this.killingBlow.method_8847(serverPlayerEntity, damageSource) ? false : this.entity.method_8914(serverPlayerEntity, entity);
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
		private final PlayerAdvancementTracker field_9666;
		private final Set<Criterion.ConditionsContainer<OnKilledCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<OnKilledCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9666 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_8993(Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_8996(Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_8994(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource) {
			List<Criterion.ConditionsContainer<OnKilledCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_9000(serverPlayerEntity, entity, damageSource)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<OnKilledCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<OnKilledCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9666);
				}
			}
		}
	}
}
