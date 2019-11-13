package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class InventoryChangedCriterion extends AbstractCriterion<InventoryChangedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("inventory_changed");

	@Override
	public Identifier getId() {
		return ID;
	}

	public InventoryChangedCriterion.Conditions method_8952(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "slots", new JsonObject());
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject2.get("occupied"));
		NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject2.get("full"));
		NumberRange.IntRange intRange3 = NumberRange.IntRange.fromJson(jsonObject2.get("empty"));
		ItemPredicate[] itemPredicates = ItemPredicate.deserializeAll(jsonObject.get("items"));
		return new InventoryChangedCriterion.Conditions(intRange, intRange2, intRange3, itemPredicates);
	}

	public void trigger(ServerPlayerEntity player, PlayerInventory inventory) {
		this.test(player.getAdvancementManager(), conditions -> conditions.matches(inventory));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.IntRange occupied;
		private final NumberRange.IntRange full;
		private final NumberRange.IntRange empty;
		private final ItemPredicate[] items;

		public Conditions(NumberRange.IntRange intRange, NumberRange.IntRange intRange2, NumberRange.IntRange intRange3, ItemPredicate[] items) {
			super(InventoryChangedCriterion.ID);
			this.occupied = intRange;
			this.full = intRange2;
			this.empty = intRange3;
			this.items = items;
		}

		public static InventoryChangedCriterion.Conditions items(ItemPredicate... items) {
			return new InventoryChangedCriterion.Conditions(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, items);
		}

		public static InventoryChangedCriterion.Conditions items(ItemConvertible... items) {
			ItemPredicate[] itemPredicates = new ItemPredicate[items.length];

			for (int i = 0; i < items.length; i++) {
				itemPredicates[i] = new ItemPredicate(
					null,
					items[i].asItem(),
					NumberRange.IntRange.ANY,
					NumberRange.IntRange.ANY,
					EnchantmentPredicate.ARRAY_OF_ANY,
					EnchantmentPredicate.ARRAY_OF_ANY,
					null,
					NbtPredicate.ANY
				);
			}

			return items(itemPredicates);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (!this.occupied.isDummy() || !this.full.isDummy() || !this.empty.isDummy()) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.add("occupied", this.occupied.toJson());
				jsonObject2.add("full", this.full.toJson());
				jsonObject2.add("empty", this.empty.toJson());
				jsonObject.add("slots", jsonObject2);
			}

			if (this.items.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (ItemPredicate itemPredicate : this.items) {
					jsonArray.add(itemPredicate.toJson());
				}

				jsonObject.add("items", jsonArray);
			}

			return jsonObject;
		}

		public boolean matches(PlayerInventory inventory) {
			int i = 0;
			int j = 0;
			int k = 0;
			List<ItemPredicate> list = Lists.<ItemPredicate>newArrayList(this.items);

			for (int l = 0; l < inventory.getInvSize(); l++) {
				ItemStack itemStack = inventory.getInvStack(l);
				if (itemStack.isEmpty()) {
					j++;
				} else {
					k++;
					if (itemStack.getCount() >= itemStack.getMaxCount()) {
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
}
