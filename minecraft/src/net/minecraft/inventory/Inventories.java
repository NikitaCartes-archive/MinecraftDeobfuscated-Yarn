package net.minecraft.inventory;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.collection.DefaultedList;

public class Inventories {
	public static ItemStack splitStack(List<ItemStack> stacks, int slot, int amount) {
		return slot >= 0 && slot < stacks.size() && !((ItemStack)stacks.get(slot)).isEmpty() && amount > 0
			? ((ItemStack)stacks.get(slot)).split(amount)
			: ItemStack.EMPTY;
	}

	public static ItemStack removeStack(List<ItemStack> stacks, int slot) {
		return slot >= 0 && slot < stacks.size() ? (ItemStack)stacks.set(slot, ItemStack.EMPTY) : ItemStack.EMPTY;
	}

	public static CompoundTag toTag(CompoundTag tag, DefaultedList<ItemStack> stacks) {
		return toTag(tag, stacks, true);
	}

	public static CompoundTag toTag(CompoundTag tag, DefaultedList<ItemStack> stacks, boolean setIfEmpty) {
		ListTag listTag = new ListTag();

		for (int i = 0; i < stacks.size(); i++) {
			ItemStack itemStack = stacks.get(i);
			if (!itemStack.isEmpty()) {
				CompoundTag compoundTag = new CompoundTag();
				compoundTag.putByte("Slot", (byte)i);
				itemStack.toTag(compoundTag);
				listTag.add(compoundTag);
			}
		}

		if (!listTag.isEmpty() || setIfEmpty) {
			tag.put("Items", listTag);
		}

		return tag;
	}

	public static void fromTag(CompoundTag tag, DefaultedList<ItemStack> stacks) {
		ListTag listTag = tag.getList("Items", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompound(i);
			int j = compoundTag.getByte("Slot") & 255;
			if (j >= 0 && j < stacks.size()) {
				stacks.set(j, ItemStack.fromTag(compoundTag));
			}
		}
	}

	/**
	 * Removes a number, not exceeding {@code maxCount}, of items from an inventory based on a predicate and returns that number.
	 * @return the number of items removed
	 * 
	 * @param dryRun whether to return the number of items which would have been removed without actually removing them
	 */
	public static int remove(Inventory inventory, Predicate<ItemStack> shouldRemove, int maxCount, boolean dryRun) {
		int i = 0;

		for (int j = 0; j < inventory.size(); j++) {
			ItemStack itemStack = inventory.getStack(j);
			int k = remove(itemStack, shouldRemove, maxCount - i, dryRun);
			if (k > 0 && !dryRun && itemStack.isEmpty()) {
				inventory.setStack(j, ItemStack.EMPTY);
			}

			i += k;
		}

		return i;
	}

	/**
	 * Removes a number, not exceeding {@code maxCount}, of items from an item stack based on a predicate and returns that number.
	 * @return the number of items removed
	 * 
	 * @param dryRun whether to return the number of items which would have been removed without actually removing them
	 */
	public static int remove(ItemStack stack, Predicate<ItemStack> shouldRemove, int maxCount, boolean dryRun) {
		if (stack.isEmpty() || !shouldRemove.test(stack)) {
			return 0;
		} else if (dryRun) {
			return stack.getCount();
		} else {
			int i = maxCount < 0 ? stack.getCount() : Math.min(maxCount, stack.getCount());
			stack.decrement(i);
			return i;
		}
	}
}
