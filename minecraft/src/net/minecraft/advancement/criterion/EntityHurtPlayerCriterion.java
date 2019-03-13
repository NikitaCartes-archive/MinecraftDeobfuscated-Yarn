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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EntityHurtPlayerCriterion implements Criterion<EntityHurtPlayerCriterion.Conditions> {
	private static final Identifier field_9589 = new Identifier("entity_hurt_player");
	private final Map<PlayerAdvancementTracker, EntityHurtPlayerCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, EntityHurtPlayerCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9589;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<EntityHurtPlayerCriterion.Conditions> conditionsContainer
	) {
		EntityHurtPlayerCriterion.Handler handler = (EntityHurtPlayerCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new EntityHurtPlayerCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_8903(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<EntityHurtPlayerCriterion.Conditions> conditionsContainer
	) {
		EntityHurtPlayerCriterion.Handler handler = (EntityHurtPlayerCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_8906(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public EntityHurtPlayerCriterion.Conditions method_8902(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DamagePredicate damagePredicate = DamagePredicate.deserialize(jsonObject.get("damage"));
		return new EntityHurtPlayerCriterion.Conditions(damagePredicate);
	}

	public void method_8901(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
		EntityHurtPlayerCriterion.Handler handler = (EntityHurtPlayerCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_8905(serverPlayerEntity, damageSource, f, g, bl);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final DamagePredicate damage;

		public Conditions(DamagePredicate damagePredicate) {
			super(EntityHurtPlayerCriterion.field_9589);
			this.damage = damagePredicate;
		}

		public static EntityHurtPlayerCriterion.Conditions method_8908(DamagePredicate.Builder builder) {
			return new EntityHurtPlayerCriterion.Conditions(builder.build());
		}

		public boolean method_8907(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
			return this.damage.method_8838(serverPlayerEntity, damageSource, f, g, bl);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("damage", this.damage.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9592;
		private final Set<Criterion.ConditionsContainer<EntityHurtPlayerCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<EntityHurtPlayerCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9592 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_8903(Criterion.ConditionsContainer<EntityHurtPlayerCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_8906(Criterion.ConditionsContainer<EntityHurtPlayerCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_8905(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
			List<Criterion.ConditionsContainer<EntityHurtPlayerCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<EntityHurtPlayerCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_8907(serverPlayerEntity, damageSource, f, g, bl)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<EntityHurtPlayerCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<EntityHurtPlayerCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9592);
				}
			}
		}
	}
}
