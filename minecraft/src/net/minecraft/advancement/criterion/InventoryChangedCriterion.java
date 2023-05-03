package net.minecraft.advancement.criterion;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class InventoryChangedCriterion extends AbstractCriterion<InventoryChangedCriterion.Conditions> {
	static final Identifier ID = new Identifier("inventory_changed");

	@Override
	public Identifier getId() {
		return ID;
	}

	public InventoryChangedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "slots", new JsonObject());
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject2.get("occupied"));
		NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject2.get("full"));
		NumberRange.IntRange intRange3 = NumberRange.IntRange.fromJson(jsonObject2.get("empty"));
		ItemPredicate[] itemPredicates = ItemPredicate.deserializeAll(jsonObject.get("items"));
		return new InventoryChangedCriterion.Conditions(lootContextPredicate, intRange, intRange2, intRange3, itemPredicates);
	}

	public void trigger(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack) {
		int i = 0;
		int j = 0;
		int k = 0;

		for (int l = 0; l < inventory.size(); l++) {
			ItemStack itemStack = inventory.getStack(l);
			if (itemStack.isEmpty()) {
				j++;
			} else {
				k++;
				if (itemStack.getCount() >= itemStack.getMaxCount()) {
					i++;
				}
			}
		}

		this.trigger(player, inventory, stack, i, j, k);
	}

	private void trigger(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack, int full, int empty, int occupied) {
		this.trigger(player, conditions -> conditions.matches(inventory, stack, full, empty, occupied));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.IntRange occupied;
		private final NumberRange.IntRange full;
		private final NumberRange.IntRange empty;
		private final ItemPredicate[] items;

		public Conditions(LootContextPredicate player, NumberRange.IntRange occupied, NumberRange.IntRange full, NumberRange.IntRange empty, ItemPredicate[] items) {
			super(InventoryChangedCriterion.ID, player);
			this.occupied = occupied;
			this.full = full;
			this.empty = empty;
			this.items = items;
		}

		public static InventoryChangedCriterion.Conditions items(ItemPredicate... items) {
			return new InventoryChangedCriterion.Conditions(
				LootContextPredicate.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, items
			);
		}

		public static InventoryChangedCriterion.Conditions items(ItemConvertible... items) {
			ItemPredicate[] itemPredicates = new ItemPredicate[items.length];

			for (int i = 0; i < items.length; i++) {
				itemPredicates[i] = new ItemPredicate(
					null,
					ImmutableSet.of(items[i].asItem()),
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
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
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

		public boolean matches(PlayerInventory inventory, ItemStack stack, int full, int empty, int occupied) {
			if (!this.full.test(full)) {
				return false;
			} else if (!this.empty.test(empty)) {
				return false;
			} else if (!this.occupied.test(occupied)) {
				return false;
			} else {
				int i = this.items.length;
				if (i == 0) {
					return true;
				} else if (i != 1) {
					List<ItemPredicate> list = new ObjectArrayList<>(this.items);
					int j = inventory.size();

					for (int k = 0; k < j; k++) {
						if (list.isEmpty()) {
							return true;
						}

						ItemStack itemStack = inventory.getStack(k);
						if (!itemStack.isEmpty()) {
							list.removeIf(item -> item.test(itemStack));
						}
					}

					return list.isEmpty();
				} else {
					return !stack.isEmpty() && this.items[0].test(stack);
				}
			}
		}
	}
}
