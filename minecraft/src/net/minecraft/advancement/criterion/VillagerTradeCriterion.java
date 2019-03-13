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
	private static final Identifier field_9762 = new Identifier("villager_trade");
	private final Map<PlayerAdvancementTracker, VillagerTradeCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, VillagerTradeCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9762;
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

		handler.method_9150(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer
	) {
		VillagerTradeCriterion.Handler handler = (VillagerTradeCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_9152(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public VillagerTradeCriterion.Conditions method_9148(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("villager"));
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		return new VillagerTradeCriterion.Conditions(entityPredicate, itemPredicate);
	}

	public void method_9146(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
		VillagerTradeCriterion.Handler handler = (VillagerTradeCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_9149(serverPlayerEntity, abstractTraderEntity, itemStack);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate item;
		private final ItemPredicate villager;

		public Conditions(EntityPredicate entityPredicate, ItemPredicate itemPredicate) {
			super(VillagerTradeCriterion.field_9762);
			this.item = entityPredicate;
			this.villager = itemPredicate;
		}

		public static VillagerTradeCriterion.Conditions any() {
			return new VillagerTradeCriterion.Conditions(EntityPredicate.ANY, ItemPredicate.ANY);
		}

		public boolean method_9154(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
			return !this.item.method_8914(serverPlayerEntity, abstractTraderEntity) ? false : this.villager.test(itemStack);
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
		private final PlayerAdvancementTracker field_9765;
		private final Set<Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9765 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_9150(Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_9152(Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_9149(ServerPlayerEntity serverPlayerEntity, AbstractTraderEntity abstractTraderEntity, ItemStack itemStack) {
			List<Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_9154(serverPlayerEntity, abstractTraderEntity, itemStack)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<VillagerTradeCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9765);
				}
			}
		}
	}
}
