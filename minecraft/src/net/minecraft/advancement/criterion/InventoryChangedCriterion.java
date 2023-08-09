package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.entry.RegistryEntryList;
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
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "slots", new JsonObject());
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject2.get("occupied"));
		NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject2.get("full"));
		NumberRange.IntRange intRange3 = NumberRange.IntRange.fromJson(jsonObject2.get("empty"));
		List<ItemPredicate> list = ItemPredicate.deserializeAll(jsonObject.get("items"));
		return new InventoryChangedCriterion.Conditions(optional, intRange, intRange2, intRange3, list);
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
		private final List<ItemPredicate> items;

		public Conditions(
			Optional<LootContextPredicate> playerPredicate,
			NumberRange.IntRange occupied,
			NumberRange.IntRange full,
			NumberRange.IntRange empty,
			List<ItemPredicate> items
		) {
			super(InventoryChangedCriterion.ID, playerPredicate);
			this.occupied = occupied;
			this.full = full;
			this.empty = empty;
			this.items = items;
		}

		public static InventoryChangedCriterion.Conditions items(ItemPredicate.Builder... items) {
			return items((ItemPredicate[])Arrays.stream(items).flatMap(builder -> builder.build().stream()).toArray(ItemPredicate[]::new));
		}

		public static InventoryChangedCriterion.Conditions items(ItemPredicate... items) {
			return new InventoryChangedCriterion.Conditions(
				Optional.empty(), NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, List.of(items)
			);
		}

		public static InventoryChangedCriterion.Conditions items(ItemConvertible... items) {
			ItemPredicate[] itemPredicates = new ItemPredicate[items.length];

			for (int i = 0; i < items.length; i++) {
				itemPredicates[i] = new ItemPredicate(
					Optional.empty(),
					Optional.of(RegistryEntryList.of(items[i].asItem().getRegistryEntry())),
					NumberRange.IntRange.ANY,
					NumberRange.IntRange.ANY,
					List.of(),
					List.of(),
					Optional.empty(),
					Optional.empty()
				);
			}

			return items(itemPredicates);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			if (!this.occupied.isDummy() || !this.full.isDummy() || !this.empty.isDummy()) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.add("occupied", this.occupied.toJson());
				jsonObject2.add("full", this.full.toJson());
				jsonObject2.add("empty", this.empty.toJson());
				jsonObject.add("slots", jsonObject2);
			}

			if (!this.items.isEmpty()) {
				jsonObject.add("items", ItemPredicate.toJson(this.items));
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
			} else if (this.items.isEmpty()) {
				return true;
			} else if (this.items.size() != 1) {
				List<ItemPredicate> list = new ObjectArrayList<>(this.items);
				int i = inventory.size();

				for (int j = 0; j < i; j++) {
					if (list.isEmpty()) {
						return true;
					}

					ItemStack itemStack = inventory.getStack(j);
					if (!itemStack.isEmpty()) {
						list.removeIf(item -> item.test(itemStack));
					}
				}

				return list.isEmpty();
			} else {
				return !stack.isEmpty() && ((ItemPredicate)this.items.get(0)).test(stack);
			}
		}
	}
}
