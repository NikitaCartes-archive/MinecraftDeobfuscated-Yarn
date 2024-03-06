package net.minecraft.inventory;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

/**
 * Contains utility methods used by {@link Inventory} implementations or for working
 * with inventories.
 */
public class Inventories {
	public static final String ITEMS_NBT_KEY = "Items";

	/**
	 * {@return the copy of the stack split from the stack at {@code slot}}
	 * 
	 * <p>This returns {@link ItemStack#EMPTY} when {@code slot} is out of bounds,
	 * the stack at the slot is empty, or when {@code amount <= 0}.
	 * 
	 * @apiNote This is used to implement {@link Inventory#removeStack(int, int)}.
	 * This should not otherwise be used directly.
	 * 
	 * @see ItemStack#split(int)
	 */
	public static ItemStack splitStack(List<ItemStack> stacks, int slot, int amount) {
		return slot >= 0 && slot < stacks.size() && !((ItemStack)stacks.get(slot)).isEmpty() && amount > 0
			? ((ItemStack)stacks.get(slot)).split(amount)
			: ItemStack.EMPTY;
	}

	/**
	 * Sets the stack at {@code slot} to {@link ItemStack#EMPTY} and returns the old stack.
	 * 
	 * <p>This returns {@link ItemStack#EMPTY} when {@code slot} is out of bounds.
	 * 
	 * @apiNote This is used to implement {@link Inventory#removeStack(int)}.
	 * This should not otherwise be used directly.
	 * 
	 * @return the stack previously at {@code slot}
	 */
	public static ItemStack removeStack(List<ItemStack> stacks, int slot) {
		return slot >= 0 && slot < stacks.size() ? (ItemStack)stacks.set(slot, ItemStack.EMPTY) : ItemStack.EMPTY;
	}

	/**
	 * Writes the inventory to {@code nbt}. This method will always write to the NBT,
	 * even if {@code stacks} only contains empty stacks.
	 * 
	 * <p>See {@link #writeNbt(NbtCompound, DefaultedList, boolean, RegistryWrapper.WrapperLookup)}
	 * for the serialization format.
	 * 
	 * @see #readNbt(NbtCompound, DefaultedList, RegistryWrapper.WrapperLookup)
	 * @see #writeNbt(NbtCompound, DefaultedList, boolean, RegistryWrapper.WrapperLookup)
	 * @return the passed {@code nbt}
	 */
	public static NbtCompound writeNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks, RegistryWrapper.WrapperLookup registries) {
		return writeNbt(nbt, stacks, true, registries);
	}

	/**
	 * Writes the inventory to {@code nbt}.
	 * 
	 * <p>The inventory is serialized as a list of non-empty item stacks.
	 * In addition, each compound has a byte entry with the key {@code Slot},
	 * indicating the slot. The list is then written to {@code nbt} under the key {@code
	 * Items}.
	 * 
	 * <p>If {@code setIfEmpty} is {@code false} and each stack in {@code stacks} is empty,
	 * then {@code nbt} will not be modified at all. Otherwise, the {@code Items} entry
	 * will always be present.
	 * 
	 * @see #readNbt(NbtCompound, DefaultedList, RegistryWrapper.WrapperLookup)
	 * @return the passed {@code nbt}
	 */
	public static NbtCompound writeNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks, boolean setIfEmpty, RegistryWrapper.WrapperLookup registries) {
		NbtList nbtList = new NbtList();

		for (int i = 0; i < stacks.size(); i++) {
			ItemStack itemStack = stacks.get(i);
			if (!itemStack.isEmpty()) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Slot", (byte)i);
				nbtList.add(itemStack.encode(registries, nbtCompound));
			}
		}

		if (!nbtList.isEmpty() || setIfEmpty) {
			nbt.put("Items", nbtList);
		}

		return nbt;
	}

	/**
	 * Reads {@code nbt} and sets the elements of {@code stacks} accordingly.
	 * 
	 * <p>See {@link #writeNbt(NbtCompound, DefaultedList, boolean, RegistryWrapper.WrapperLookup)}
	 * for the serialization format. If the slot is out of bounds, it is ignored.
	 * 
	 * @see #writeNbt(NbtCompound, DefaultedList, RegistryWrapper.WrapperLookup)
	 * @see #writeNbt(NbtCompound, DefaultedList, boolean, RegistryWrapper.WrapperLookup)
	 */
	public static void readNbt(NbtCompound nbt, DefaultedList<ItemStack> stacks, RegistryWrapper.WrapperLookup registries) {
		NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot") & 255;
			if (j >= 0 && j < stacks.size()) {
				stacks.set(j, (ItemStack)ItemStack.fromNbt(registries, nbtCompound).orElse(ItemStack.EMPTY));
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
