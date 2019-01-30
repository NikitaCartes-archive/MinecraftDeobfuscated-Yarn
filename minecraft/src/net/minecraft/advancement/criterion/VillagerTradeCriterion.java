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
import net.minecraft.entity.passive.AbstractVillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class VillagerTradeCriterion implements Criterion<VillagerTradeCriterion.Conditions> {
	private static final Identifier ID = new Identifier("villager_trade");
	private final Map<PlayerAdvancementTracker, VillagerTradeCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, VillagerTradeCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer
	) {
		VillagerTradeCriterion.Handler handler = (VillagerTradeCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new VillagerTradeCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer
	) {
		VillagerTradeCriterion.Handler handler = (VillagerTradeCriterion.Handler)this.handlers.get(playerAdvancementTracker);
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

	public VillagerTradeCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("villager"));
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		return new VillagerTradeCriterion.Conditions(entityPredicate, itemPredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, AbstractVillagerEntity abstractVillagerEntity, ItemStack itemStack) {
		VillagerTradeCriterion.Handler handler = (VillagerTradeCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(serverPlayerEntity, abstractVillagerEntity, itemStack);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate item;
		private final ItemPredicate villager;

		public Conditions(EntityPredicate entityPredicate, ItemPredicate itemPredicate) {
			super(VillagerTradeCriterion.ID);
			this.item = entityPredicate;
			this.villager = itemPredicate;
		}

		public static VillagerTradeCriterion.Conditions any() {
			return new VillagerTradeCriterion.Conditions(EntityPredicate.ANY, ItemPredicate.ANY);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, AbstractVillagerEntity abstractVillagerEntity, ItemStack itemStack) {
			return !this.item.test(serverPlayerEntity, abstractVillagerEntity) ? false : this.villager.test(itemStack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.villager.serialize());
			jsonObject.add("villager", this.item.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerPlayerEntity serverPlayerEntity, AbstractVillagerEntity abstractVillagerEntity, ItemStack itemStack) {
			List<Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(serverPlayerEntity, abstractVillagerEntity, itemStack)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
