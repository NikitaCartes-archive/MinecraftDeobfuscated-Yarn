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
	private static final Identifier ID = new Identifier("player_hurt_entity");
	private final Map<PlayerAdvancementTracker, PlayerHurtEntityCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, PlayerHurtEntityCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainer) {
		PlayerHurtEntityCriterion.Handler handler = (PlayerHurtEntityCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new PlayerHurtEntityCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainer) {
		PlayerHurtEntityCriterion.Handler handler = (PlayerHurtEntityCriterion.Handler)this.handlers.get(manager);
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

	public PlayerHurtEntityCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DamagePredicate damagePredicate = DamagePredicate.deserialize(jsonObject.get("damage"));
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
		return new PlayerHurtEntityCriterion.Conditions(damagePredicate, entityPredicate);
	}

	public void trigger(ServerPlayerEntity player, Entity entity, DamageSource source, float dealt, float taken, boolean blocked) {
		PlayerHurtEntityCriterion.Handler handler = (PlayerHurtEntityCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(player, entity, source, dealt, taken, blocked);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final DamagePredicate damage;
		private final EntityPredicate entity;

		public Conditions(DamagePredicate damage, EntityPredicate entity) {
			super(PlayerHurtEntityCriterion.ID);
			this.damage = damage;
			this.entity = entity;
		}

		public static PlayerHurtEntityCriterion.Conditions create(DamagePredicate.Builder builder) {
			return new PlayerHurtEntityCriterion.Conditions(builder.build(), EntityPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity player, Entity entity, DamageSource source, float dealt, float taken, boolean blocked) {
			return !this.damage.test(player, source, dealt, taken, blocked) ? false : this.entity.test(player, entity);
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
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerPlayerEntity player, Entity entity, DamageSource source, float dealt, float taken, boolean blocked) {
			List<Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(player, entity, source, dealt, taken, blocked)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<PlayerHurtEntityCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
