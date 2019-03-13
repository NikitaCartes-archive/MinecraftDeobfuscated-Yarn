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
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CuredZombieVillagerCriterion implements Criterion<CuredZombieVillagerCriterion.Conditions> {
	private static final Identifier field_9514 = new Identifier("cured_zombie_villager");
	private final Map<PlayerAdvancementTracker, CuredZombieVillagerCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, CuredZombieVillagerCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9514;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer
	) {
		CuredZombieVillagerCriterion.Handler handler = (CuredZombieVillagerCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new CuredZombieVillagerCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_8832(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer
	) {
		CuredZombieVillagerCriterion.Handler handler = (CuredZombieVillagerCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_8834(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public CuredZombieVillagerCriterion.Conditions method_8830(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("zombie"));
		EntityPredicate entityPredicate2 = EntityPredicate.deserialize(jsonObject.get("villager"));
		return new CuredZombieVillagerCriterion.Conditions(entityPredicate, entityPredicate2);
	}

	public void method_8831(ServerPlayerEntity serverPlayerEntity, ZombieEntity zombieEntity, VillagerEntity villagerEntity) {
		CuredZombieVillagerCriterion.Handler handler = (CuredZombieVillagerCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_8835(serverPlayerEntity, zombieEntity, villagerEntity);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate field_9518;
		private final EntityPredicate field_9519;

		public Conditions(EntityPredicate entityPredicate, EntityPredicate entityPredicate2) {
			super(CuredZombieVillagerCriterion.field_9514);
			this.field_9518 = entityPredicate;
			this.field_9519 = entityPredicate2;
		}

		public static CuredZombieVillagerCriterion.Conditions any() {
			return new CuredZombieVillagerCriterion.Conditions(EntityPredicate.ANY, EntityPredicate.ANY);
		}

		public boolean method_8837(ServerPlayerEntity serverPlayerEntity, ZombieEntity zombieEntity, VillagerEntity villagerEntity) {
			return !this.field_9518.method_8914(serverPlayerEntity, zombieEntity) ? false : this.field_9519.method_8914(serverPlayerEntity, villagerEntity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("zombie", this.field_9518.serialize());
			jsonObject.add("villager", this.field_9519.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9517;
		private final Set<Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9517 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_8832(Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_8834(Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_8835(ServerPlayerEntity serverPlayerEntity, ZombieEntity zombieEntity, VillagerEntity villagerEntity) {
			List<Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_8837(serverPlayerEntity, zombieEntity, villagerEntity)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9517);
				}
			}
		}
	}
}
