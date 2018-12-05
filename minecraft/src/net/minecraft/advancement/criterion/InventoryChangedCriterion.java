package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.NumberRange;

public class InventoryChangedCriterion implements Criterion<InventoryChangedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("inventory_changed");
	private final Map<ServerAdvancementManager, InventoryChangedCriterion.Handler> handlers = Maps.<ServerAdvancementManager, InventoryChangedCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void addCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<InventoryChangedCriterion.Conditions> conditionsContainer
	) {
		InventoryChangedCriterion.Handler handler = (InventoryChangedCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler == null) {
			handler = new InventoryChangedCriterion.Handler(serverAdvancementManager);
			this.handlers.put(serverAdvancementManager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<InventoryChangedCriterion.Conditions> conditionsContainer
	) {
		InventoryChangedCriterion.Handler handler = (InventoryChangedCriterion.Handler)this.handlers.get(serverAdvancementManager);
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

	public InventoryChangedCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "slots", new JsonObject());
		NumberRange.Integer integer = NumberRange.Integer.fromJson(jsonObject2.get("occupied"));
		NumberRange.Integer integer2 = NumberRange.Integer.fromJson(jsonObject2.get("full"));
		NumberRange.Integer integer3 = NumberRange.Integer.fromJson(jsonObject2.get("empty"));
		ItemPredicate[] itemPredicates = ItemPredicate.deserializeAll(jsonObject.get("items"));
		return new InventoryChangedCriterion.Conditions(integer, integer2, integer3, itemPredicates);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, PlayerInventory playerInventory) {
		InventoryChangedCriterion.Handler handler = (InventoryChangedCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(playerInventory);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.Integer occupied;
		private final NumberRange.Integer full;
		private final NumberRange.Integer empty;
		private final ItemPredicate[] items;

		public Conditions(NumberRange.Integer integer, NumberRange.Integer integer2, NumberRange.Integer integer3, ItemPredicate[] itemPredicates) {
			super(InventoryChangedCriterion.ID);
			this.occupied = integer;
			this.full = integer2;
			this.empty = integer3;
			this.items = itemPredicates;
		}

		public static InventoryChangedCriterion.Conditions items(ItemPredicate... itemPredicates) {
			return new InventoryChangedCriterion.Conditions(NumberRange.Integer.ANY, NumberRange.Integer.ANY, NumberRange.Integer.ANY, itemPredicates);
		}

		public static InventoryChangedCriterion.Conditions items(ItemContainer... itemContainers) {
			ItemPredicate[] itemPredicates = new ItemPredicate[itemContainers.length];

			for (int i = 0; i < itemContainers.length; i++) {
				itemPredicates[i] = new ItemPredicate(
					null, itemContainers[i].getItem(), NumberRange.Integer.ANY, NumberRange.Integer.ANY, new EnchantmentPredicate[0], null, NbtPredicate.ANY
				);
			}

			return items(itemPredicates);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			if (!this.occupied.isDummy() || !this.full.isDummy() || !this.empty.isDummy()) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.add("occupied", this.occupied.serialize());
				jsonObject2.add("full", this.full.serialize());
				jsonObject2.add("empty", this.empty.serialize());
				jsonObject.add("slots", jsonObject2);
			}

			if (this.items.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (ItemPredicate itemPredicate : this.items) {
					jsonArray.add(itemPredicate.serialize());
				}

				jsonObject.add("items", jsonArray);
			}

			return jsonObject;
		}

		public boolean matches(PlayerInventory playerInventory) {
			int i = 0;
			int j = 0;
			int k = 0;
			List<ItemPredicate> list = Lists.<ItemPredicate>newArrayList(this.items);

			for (int l = 0; l < playerInventory.getInvSize(); l++) {
				ItemStack itemStack = playerInventory.getInvStack(l);
				if (itemStack.isEmpty()) {
					j++;
				} else {
					k++;
					if (itemStack.getAmount() >= itemStack.getMaxAmount()) {
						i++;
					}

					Iterator<ItemPredicate> iterator = list.iterator();

					while (iterator.hasNext()) {
						ItemPredicate itemPredicate = (ItemPredicate)iterator.next();
						if (itemPredicate.test(itemStack)) {
							iterator.remove();
						}
					}
				}
			}

			if (!this.full.test(i)) {
				return false;
			} else if (!this.empty.test(j)) {
				return false;
			} else {
				return !this.occupied.test(k) ? false : list.isEmpty();
			}
		}
	}

	static class Handler {
		private final ServerAdvancementManager manager;
		private final Set<Criterion.ConditionsContainer<InventoryChangedCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<InventoryChangedCriterion.Conditions>>newHashSet();

		public Handler(ServerAdvancementManager serverAdvancementManager) {
			this.manager = serverAdvancementManager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<InventoryChangedCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<InventoryChangedCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(PlayerInventory playerInventory) {
			List<Criterion.ConditionsContainer<InventoryChangedCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<InventoryChangedCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(playerInventory)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<InventoryChangedCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<InventoryChangedCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
