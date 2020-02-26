package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.util.DefaultedList;

public class BasicInventory implements Inventory, RecipeInputProvider {
	private final int size;
	private final DefaultedList<ItemStack> stackList;
	private List<InventoryListener> listeners;

	public BasicInventory(int size) {
		this.size = size;
		this.stackList = DefaultedList.ofSize(size, ItemStack.EMPTY);
	}

	public BasicInventory(ItemStack... items) {
		this.size = items.length;
		this.stackList = DefaultedList.copyOf(ItemStack.EMPTY, items);
	}

	public void addListener(InventoryListener listener) {
		if (this.listeners == null) {
			this.listeners = Lists.<InventoryListener>newArrayList();
		}

		this.listeners.add(listener);
	}

	public void removeListener(InventoryListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= 0 && slot < this.stackList.size() ? this.stackList.get(slot) : ItemStack.EMPTY;
	}

	/**
	 * Clears this inventory and return all the non-empty stacks in a list.
	 */
	public List<ItemStack> clearToList() {
		List<ItemStack> list = (List<ItemStack>)this.stackList.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
		this.clear();
		return list;
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack itemStack = Inventories.splitStack(this.stackList, slot, amount);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	public ItemStack poll(Item item, int count) {
		ItemStack itemStack = new ItemStack(item, 0);

		for (int i = this.size - 1; i >= 0; i--) {
			ItemStack itemStack2 = this.getInvStack(i);
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

	public ItemStack add(ItemStack stack) {
		ItemStack itemStack = stack.copy();
		this.addToExistingSlot(itemStack);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.addToNewSlot(itemStack);
			return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
		}
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		ItemStack itemStack = this.stackList.get(slot);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.stackList.set(slot, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.stackList.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}

		this.markDirty();
	}

	@Override
	public int getInvSize() {
		return this.size;
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.stackList) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void markDirty() {
		if (this.listeners != null) {
			for (InventoryListener inventoryListener : this.listeners) {
				inventoryListener.onInvChange(this);
			}
		}
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		this.stackList.clear();
		this.markDirty();
	}

	@Override
	public void provideRecipeInputs(RecipeFinder finder) {
		for (ItemStack itemStack : this.stackList) {
			finder.addItem(itemStack);
		}
	}

	public String toString() {
		return ((List)this.stackList.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList())).toString();
	}

	private void addToNewSlot(ItemStack stack) {
		for (int i = 0; i < this.size; i++) {
			ItemStack itemStack = this.getInvStack(i);
			if (itemStack.isEmpty()) {
				this.setInvStack(i, stack.copy());
				stack.setCount(0);
				return;
			}
		}
	}

	private void addToExistingSlot(ItemStack stack) {
		for (int i = 0; i < this.size; i++) {
			ItemStack itemStack = this.getInvStack(i);
			if (this.method_24825(itemStack, stack)) {
				this.transfer(stack, itemStack);
				if (stack.isEmpty()) {
					return;
				}
			}
		}
	}

	private boolean method_24825(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack.getItem() == itemStack2.getItem() && ItemStack.areTagsEqual(itemStack, itemStack2);
	}

	private void transfer(ItemStack source, ItemStack target) {
		int i = Math.min(this.getInvMaxStackAmount(), target.getMaxCount());
		int j = Math.min(source.getCount(), i - target.getCount());
		if (j > 0) {
			target.increment(j);
			source.decrement(j);
			this.markDirty();
		}
	}
}
