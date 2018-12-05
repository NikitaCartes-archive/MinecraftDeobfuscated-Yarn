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
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;

public class ItemDurabilityChangedCriterion implements Criterion<ItemDurabilityChangedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("item_durability_changed");
	private final Map<ServerAdvancementManager, ItemDurabilityChangedCriterion.Handler> handlers = Maps.<ServerAdvancementManager, ItemDurabilityChangedCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void addCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer
	) {
		ItemDurabilityChangedCriterion.Handler handler = (ItemDurabilityChangedCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler == null) {
			handler = new ItemDurabilityChangedCriterion.Handler(serverAdvancementManager);
			this.handlers.put(serverAdvancementManager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer
	) {
		ItemDurabilityChangedCriterion.Handler handler = (ItemDurabilityChangedCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(serverAdvancementManager);
			}
		}
	}

	@Override
	public void removePlayer(ServerAdvancementManager serverAdvancementManager) {
		this.handlers.remove(serverAdvancementManager);
	}

	public ItemDurabilityChangedCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		NumberRange.Integer integer = NumberRange.Integer.fromJson(jsonObject.get("durability"));
		NumberRange.Integer integer2 = NumberRange.Integer.fromJson(jsonObject.get("delta"));
		return new ItemDurabilityChangedCriterion.Conditions(itemPredicate, integer, integer2);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, int i) {
		ItemDurabilityChangedCriterion.Handler handler = (ItemDurabilityChangedCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(itemStack, i);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;
		private final NumberRange.Integer durability;
		private final NumberRange.Integer delta;

		public Conditions(ItemPredicate itemPredicate, NumberRange.Integer integer, NumberRange.Integer integer2) {
			super(ItemDurabilityChangedCriterion.ID);
			this.item = itemPredicate;
			this.durability = integer;
			this.delta = integer2;
		}

		public static ItemDurabilityChangedCriterion.Conditions method_8967(ItemPredicate itemPredicate, NumberRange.Integer integer) {
			return new ItemDurabilityChangedCriterion.Conditions(itemPredicate, integer, NumberRange.Integer.ANY);
		}

		public boolean matches(ItemStack itemStack, int i) {
			if (!this.item.test(itemStack)) {
				return false;
			} else {
				return !this.durability.test(itemStack.getDurability() - i) ? false : this.delta.test(itemStack.getDamage() - i);
			}
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.serialize());
			jsonObject.add("durability", this.durability.serialize());
			jsonObject.add("delta", this.delta.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final ServerAdvancementManager manager;
		private final Set<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>>newHashSet();

		public Handler(ServerAdvancementManager serverAdvancementManager) {
			this.manager = serverAdvancementManager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ItemStack itemStack, int i) {
			List<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(itemStack, i)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
