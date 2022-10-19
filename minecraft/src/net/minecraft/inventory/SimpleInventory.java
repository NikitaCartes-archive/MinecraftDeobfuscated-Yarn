package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.util.collection.DefaultedList;

public class SimpleInventory implements Inventory, RecipeInputProvider {
	private final int size;
	private final DefaultedList<ItemStack> stacks;
	@Nullable
	private List<InventoryChangedListener> listeners;

	public SimpleInventory(int size) {
		this.size = size;
		this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
	}

	public SimpleInventory(ItemStack... items) {
		this.size = items.length;
		this.stacks = DefaultedList.copyOf(ItemStack.EMPTY, items);
	}

	public void addListener(InventoryChangedListener listener) {
		if (this.listeners == null) {
			this.listeners = Lists.<InventoryChangedListener>newArrayList();
		}

		this.listeners.add(listener);
	}

	public void removeListener(InventoryChangedListener listener) {
		if (this.listeners != null) {
			this.listeners.remove(listener);
		}
	}

	@Override
	public ItemStack getStack(int slot) {
		return slot >= 0 && slot < this.stacks.size() ? this.stacks.get(slot) : ItemStack.EMPTY;
	}

	/**
	 * Clears this inventory and return all the non-empty stacks in a list.
	 */
	public List<ItemStack> clearToList() {
		List<ItemStack> list = (List<ItemStack>)this.stacks.stream().filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
		this.clear();
		return list;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack itemStack = Inventories.splitStack(this.stacks, slot, amount);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	/**
	 * Searches this inventory for the specified item and removes the given amount from this inventory.
	 * 
	 * @return the stack of removed items
	 */
	public ItemStack removeItem(Item item, int count) {
		ItemStack itemStack = new ItemStack(item, 0);

		for (int i = this.size - 1; i >= 0; i--) {
			ItemStack itemStack2 = this.getStack(i);
			if (itemStack2.getItem().equals(item)) {
				int j = count - itemStack.getCount();
				ItemStack itemStack3 = itemStack2.split(j);
				itemStack.increment(itemStack3.getCount());
				if (itemStack.getCount() == count) {
					break;
				}
			}
		}

		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	public ItemStack addStack(ItemStack stack) {
		ItemStack itemStack = stack.copy();
		this.addToExistingSlot(itemStack);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.addToNewSlot(itemStack);
			return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
		}
	}

	public boolean canInsert(ItemStack stack) {
		boolean bl = false;

		for (ItemStack itemStack : this.stacks) {
			if (itemStack.isEmpty() || ItemStack.canCombine(itemStack, stack) && itemStack.getCount() < itemStack.getMaxCount()) {
				bl = true;
				break;
			}
		}

		return bl;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack itemStack = this.stacks.get(slot);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.stacks.set(slot, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.stacks.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}

		this.markDirty();
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.stacks) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void markDirty() {
		if (this.listeners != null) {
			for (InventoryChangedListener inventoryChangedListener : this.listeners) {
				inventoryChangedListener.onInventoryChanged(this);
			}
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		this.stacks.clear();
		this.markDirty();
	}

	@Override
	public void provideRecipeInputs(RecipeMatcher finder) {
		for (ItemStack itemStack : this.stacks) {
			finder.addInput(itemStack);
		}
	}

	public String toString() {
		return ((List)this.stacks.stream().filter(stack -> !stack.isEmpty()).collect(Collectors.toList())).toString();
	}

	private void addToNewSlot(ItemStack stack) {
		for (int i = 0; i < this.size; i++) {
			ItemStack itemStack = this.getStack(i);
			if (itemStack.isEmpty()) {
				this.setStack(i, stack.copy());
				stack.setCount(0);
				return;
			}
		}
	}

	private void addToExistingSlot(ItemStack stack) {
		for (int i = 0; i < this.size; i++) {
			ItemStack itemStack = this.getStack(i);
			if (ItemStack.canCombine(itemStack, stack)) {
				this.transfer(stack, itemStack);
				if (stack.isEmpty()) {
					return;
				}
			}
		}
	}

	private void transfer(ItemStack source, ItemStack target) {
		int i = Math.min(this.getMaxCountPerStack(), target.getMaxCount());
		int j = Math.min(source.getCount(), i - target.getCount());
		if (j > 0) {
			target.increment(j);
			source.decrement(j);
			this.markDirty();
		}
	}

	public void readNbtList(NbtList nbtList) {
		this.clear();

		for (int i = 0; i < nbtList.size(); i++) {
			ItemStack itemStack = ItemStack.fromNbt(nbtList.getCompound(i));
			if (!itemStack.isEmpty()) {
				this.addStack(itemStack);
			}
		}
	}

	public NbtList toNbtList() {
		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.size(); i++) {
			ItemStack itemStack = this.getStack(i);
			if (!itemStack.isEmpty()) {
				nbtList.add(itemStack.writeNbt(new NbtCompound()));
			}
		}

		return nbtList;
	}
}
