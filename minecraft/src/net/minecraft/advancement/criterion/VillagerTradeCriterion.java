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
import net.minecraft.entity.passive.AbstractTraderEntity;
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
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer) {
		VillagerTradeCriterion.Handler handler = (VillagerTradeCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new VillagerTradeCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer) {
		VillagerTradeCriterion.Handler handler = (VillagerTradeCriterion.Handler)this.handlers.get(manager);
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

	public VillagerTradeCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("villager"));
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new VillagerTradeCriterion.Conditions(entityPredicate, itemPredicate);
	}

	public void handle(ServerPlayerEntity player, AbstractTraderEntity trader, ItemStack stack) {
		VillagerTradeCriterion.Handler handler = (VillagerTradeCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(player, trader, stack);
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

		public boolean matches(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
			return !this.item.test(serverPlayerEntity, abstractTraderEntity) ? false : this.villager.test(itemStack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.villager.toJson());
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

		public void handle(ServerPlayerEntity villager, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
			List<Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(villager, abstractTraderEntity, itemStack)) {
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
