package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryChangedCriterion extends AbstractCriterion<InventoryChangedCriterion.Conditions> {
	@Override
	public Codec<InventoryChangedCriterion.Conditions> getConditionsCodec() {
		return InventoryChangedCriterion.Conditions.CODEC;
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

	public static record Conditions(Optional<LootContextPredicate> player, InventoryChangedCriterion.Conditions.Slots slots, List<ItemPredicate> items)
		implements AbstractCriterion.Conditions {
		public static final Codec<InventoryChangedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(InventoryChangedCriterion.Conditions::player),
						InventoryChangedCriterion.Conditions.Slots.CODEC
							.optionalFieldOf("slots", InventoryChangedCriterion.Conditions.Slots.ANY)
							.forGetter(InventoryChangedCriterion.Conditions::slots),
						ItemPredicate.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(InventoryChangedCriterion.Conditions::items)
					)
					.apply(instance, InventoryChangedCriterion.Conditions::new)
		);

		public static AdvancementCriterion<InventoryChangedCriterion.Conditions> items(ItemPredicate.Builder... items) {
			return items((ItemPredicate[])Stream.of(items).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
		}

		public static AdvancementCriterion<InventoryChangedCriterion.Conditions> items(ItemPredicate... items) {
			return Criteria.INVENTORY_CHANGED
				.create(new InventoryChangedCriterion.Conditions(Optional.empty(), InventoryChangedCriterion.Conditions.Slots.ANY, List.of(items)));
		}

		public static AdvancementCriterion<InventoryChangedCriterion.Conditions> items(ItemConvertible... items) {
			ItemPredicate[] itemPredicates = new ItemPredicate[items.length];

			for (int i = 0; i < items.length; i++) {
				itemPredicates[i] = new ItemPredicate(
					Optional.of(RegistryEntryList.of(items[i].asItem().getRegistryEntry())), NumberRange.IntRange.ANY, ComponentPredicate.EMPTY, Map.of()
				);
			}

			return items(itemPredicates);
		}

		public boolean matches(PlayerInventory inventory, ItemStack stack, int full, int empty, int occupied) {
			if (!this.slots.test(full, empty, occupied)) {
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

		public static record Slots(NumberRange.IntRange occupied, NumberRange.IntRange full, NumberRange.IntRange empty) {
			public static final Codec<InventoryChangedCriterion.Conditions.Slots> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
							NumberRange.IntRange.CODEC.optionalFieldOf("occupied", NumberRange.IntRange.ANY).forGetter(InventoryChangedCriterion.Conditions.Slots::occupied),
							NumberRange.IntRange.CODEC.optionalFieldOf("full", NumberRange.IntRange.ANY).forGetter(InventoryChangedCriterion.Conditions.Slots::full),
							NumberRange.IntRange.CODEC.optionalFieldOf("empty", NumberRange.IntRange.ANY).forGetter(InventoryChangedCriterion.Conditions.Slots::empty)
						)
						.apply(instance, InventoryChangedCriterion.Conditions.Slots::new)
			);
			public static final InventoryChangedCriterion.Conditions.Slots ANY = new InventoryChangedCriterion.Conditions.Slots(
				NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY
			);

			public boolean test(int full, int empty, int occupied) {
				if (!this.full.test(full)) {
					return false;
				} else {
					return !this.empty.test(empty) ? false : this.occupied.test(occupied);
				}
			}
		}
	}
}
