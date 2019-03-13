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
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerHurtEntityCriterion implements Criterion<PlayerHurtEntityCriterion.Conditions> {
	private static final Identifier field_9732 = new Identifier("player_hurt_entity");
	private final Map<PlayerAdvancementTracker, PlayerHurtEntityCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, PlayerHurtEntityCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9732;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainer
	) {
		PlayerHurtEntityCriterion.Handler handler = (PlayerHurtEntityCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new PlayerHurtEntityCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_9099(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainer
	) {
		PlayerHurtEntityCriterion.Handler handler = (PlayerHurtEntityCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_9102(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public PlayerHurtEntityCriterion.Conditions method_9098(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DamagePredicate damagePredicate = DamagePredicate.deserialize(jsonObject.get("damage"));
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("entity"));
		return new PlayerHurtEntityCriterion.Conditions(damagePredicate, entityPredicate);
	}

	public void method_9097(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource, float f, float g, boolean bl) {
		PlayerHurtEntityCriterion.Handler handler = (PlayerHurtEntityCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_9101(serverPlayerEntity, entity, damageSource, f, g, bl);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final DamagePredicate damage;
		private final EntityPredicate entity;

		public Conditions(DamagePredicate damagePredicate, EntityPredicate entityPredicate) {
			super(PlayerHurtEntityCriterion.field_9732);
			this.damage = damagePredicate;
			this.entity = entityPredicate;
		}

		public static PlayerHurtEntityCriterion.Conditions method_9103(DamagePredicate.Builder builder) {
			return new PlayerHurtEntityCriterion.Conditions(builder.build(), EntityPredicate.ANY);
		}

		public boolean method_9104(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource, float f, float g, boolean bl) {
			return !this.damage.method_8838(serverPlayerEntity, damageSource, f, g, bl) ? false : this.entity.method_8914(serverPlayerEntity, entity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("damage", this.damage.serialize());
			jsonObject.add("entity", this.entity.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9735;
		private final Set<Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9735 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_9099(Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_9102(Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_9101(ServerPlayerEntity serverPlayerEntity, Entity entity, DamageSource damageSource, float f, float g, boolean bl) {
			List<Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_9104(serverPlayerEntity, entity, damageSource, f, g, bl)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9735);
				}
			}
		}
	}
}
