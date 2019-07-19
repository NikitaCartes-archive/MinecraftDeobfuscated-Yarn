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

	public void addListener(InventoryListener inventoryListener) {
		if (this.listeners == null) {
			this.listeners = Lists.<InventoryListener>newArrayList();
		}

		this.listeners.add(inventoryListener);
	}

	public void removeListener(InventoryListener inventoryListener) {
		this.listeners.remove(inventoryListener);
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= 0 && slot < this.stackList.size() ? this.stackList.get(slot) : ItemStack.EMPTY;
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

	public ItemStack add(ItemStack itemStack) {
		ItemStack itemStack2 = itemStack.copy();
		this.method_20634(itemStack2);
		if (itemStack2.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.method_20633(itemStack2);
			return itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2;
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
	public void provideRecipeInputs(RecipeFinder recipeFinder) {
		for (ItemStack itemStack : this.stackList) {
			recipeFinder.addItem(itemStack);
		}
	}

	public String toString() {
		return ((List)this.stackList.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList())).toString();
	}

	private void method_20633(ItemStack itemStack) {
		for (int i = 0; i < this.size; i++) {
			ItemStack itemStack2 = this.getInvStack(i);
			if (itemStack2.isEmpty()) {
				this.setInvStack(i, itemStack.copy());
				itemStack.setCount(0);
				return;
			}
		}
	}

	private void method_20634(ItemStack itemStack) {
		for (int i = 0; i < this.size; i++) {
			ItemStack itemStack2 = this.getInvStack(i);
			if (ItemStack.areItemsEqualIgnoreDamage(itemStack2, itemStack)) {
				this.method_20632(itemStack, itemStack2);
				if (itemStack.isEmpty()) {
					return;
				}
			}
		}
	}

	private void method_20632(ItemStack itemStack, ItemStack itemStack2) {
		int i = Math.min(this.getInvMaxStackAmount(), itemStack2.getMaxCount());
		int j = Math.min(itemStack.getCount(), i - itemStack2.getCount());
		if (j > 0) {
			itemStack2.increment(j);
			itemStack.decrement(j);
			this.markDirty();
		}
	}
}
