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
		PlayerAdvancementTracker manager, Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer
	) {
		CuredZombieVillagerCriterion.Handler handler = (CuredZombieVillagerCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new CuredZombieVillagerCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer) {
		CuredZombieVillagerCriterion.Handler handler = (CuredZombieVillagerCriterion.Handler)this.handlers.get(manager);
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

	public CuredZombieVillagerCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("zombie"));
		EntityPredicate entityPredicate2 = EntityPredicate.fromJson(jsonObject.get("villager"));
		return new CuredZombieVillagerCriterion.Conditions(entityPredicate, entityPredicate2);
	}

	public void trigger(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
		CuredZombieVillagerCriterion.Handler handler = (CuredZombieVillagerCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(player, zombie, villager);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate zombie;
		private final EntityPredicate villager;

		public Conditions(EntityPredicate zombie, EntityPredicate villager) {
			super(CuredZombieVillagerCriterion.ID);
			this.zombie = zombie;
			this.villager = villager;
		}

		public static CuredZombieVillagerCriterion.Conditions any() {
			return new CuredZombieVillagerCriterion.Conditions(EntityPredicate.ANY, EntityPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
			return !this.zombie.test(player, zombie) ? false : this.villager.test(player, villager);
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

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
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

		public void handle(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
			List<Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<CuredZombieVillagerCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(player, zombie, villager)) {
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
