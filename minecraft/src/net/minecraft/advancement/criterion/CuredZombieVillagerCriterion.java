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
	private static final Identifier ID = new Identifier("cured_zombie_villager");
	private final Map<PlayerAdvancementTracker, CuredZombieVillagerCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, CuredZombieVillagerCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
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

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer
	) {
		CuredZombieVillagerCriterion.Handler handler = (CuredZombieVillagerCriterion.Handler)this.handlers.get(playerAdvancementTracker);
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

	public CuredZombieVillagerCriterion.Conditions method_8830(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("zombie"));
		EntityPredicate entityPredicate2 = EntityPredicate.deserialize(jsonObject.get("villager"));
		return new CuredZombieVillagerCriterion.Conditions(entityPredicate, entityPredicate2);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, ZombieEntity zombieEntity, VillagerEntity villagerEntity) {
		CuredZombieVillagerCriterion.Handler handler = (CuredZombieVillagerCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(serverPlayerEntity, zombieEntity, villagerEntity);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate zombie;
		private final EntityPredicate villager;

		public Conditions(EntityPredicate entityPredicate, EntityPredicate entityPredicate2) {
			super(CuredZombieVillagerCriterion.ID);
			this.zombie = entityPredicate;
			this.villager = entityPredicate2;
		}

		public static CuredZombieVillagerCriterion.Conditions any() {
			return new CuredZombieVillagerCriterion.Conditions(EntityPredicate.ANY, EntityPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, ZombieEntity zombieEntity, VillagerEntity villagerEntity) {
			return !this.zombie.test(serverPlayerEntity, zombieEntity) ? false : this.villager.test(serverPlayerEntity, villagerEntity);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("zombie", this.zombie.serialize());
			jsonObject.add("villager", this.villager.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerPlayerEntity serverPlayerEntity, ZombieEntity zombieEntity, VillagerEntity villagerEntity) {
			List<Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(serverPlayerEntity, zombieEntity, villagerEntity)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
