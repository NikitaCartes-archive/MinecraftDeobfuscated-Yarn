package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.util.DefaultedList;

public class BasicInventory implements Inventory, RecipeInputProvider {
	private final int size;
	private final DefaultedList<ItemStack> stackList;
	private List<InventoryListener> listeners;

	public BasicInventory(int i) {
		this.size = i;
		this.stackList = DefaultedList.create(i, ItemStack.EMPTY);
	}

	public BasicInventory(ItemStack... itemStacks) {
		this.size = itemStacks.length;
		this.stackList = DefaultedList.create(ItemStack.EMPTY, itemStacks);
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
	public ItemStack getInvStack(int i) {
		return i >= 0 && i < this.stackList.size() ? this.stackList.get(i) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		ItemStack itemStack = Inventories.splitStack(this.stackList, i, j);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}

		return itemStack;
	}

	public ItemStack add(ItemStack itemStack) {
		ItemStack itemStack2 = itemStack.copy();

		for (int i = 0; i < this.size; i++) {
			ItemStack itemStack3 = this.getInvStack(i);
			if (itemStack3.isEmpty()) {
				this.setInvStack(i, itemStack2);
				this.markDirty();
				return ItemStack.EMPTY;
			}

			if (ItemStack.areItemsEqualIgnoreDamage(itemStack3, itemStack2)) {
				int j = Math.min(this.getInvMaxStackAmount(), itemStack3.getMaxCount());
				int k = Math.min(itemStack2.getCount(), j - itemStack3.getCount());
				if (k > 0) {
					itemStack3.increment(k);
					itemStack2.decrement(k);
					if (itemStack2.isEmpty()) {
						this.markDirty();
						return ItemStack.EMPTY;
					}
				}
			}
		}

		if (itemStack2.getCount() != itemStack.getCount()) {
			this.markDirty();
		}

		return itemStack2;
	}

	@Override
	public ItemStack removeInvStack(int i) {
		ItemStack itemStack = this.stackList.get(i);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.stackList.set(i, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		this.stackList.set(i, itemStack);
		if (!itemStack.isEmpty() && itemStack.getCount() > this.getInvMaxStackAmount()) {
			itemStack.setCount(this.getInvMaxStackAmount());
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
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return true;
	}

	@Override
	public void clear() {
		this.stackList.clear();
	}

	@Override
	public void provideRecipeInputs(RecipeFinder recipeFinder) {
		for (ItemStack itemStack : this.stackList) {
			recipeFinder.addItem(itemStack);
		}
	}
}
