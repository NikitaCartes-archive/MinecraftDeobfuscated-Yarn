package net.minecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

public class CraftingInventory implements Inventory, RecipeInputProvider {
	private final DefaultedList<ItemStack> stacks;
	private final int width;
	private final int height;
	private final ScreenHandler handler;

	public CraftingInventory(ScreenHandler handler, int width, int height) {
		this.stacks = DefaultedList.ofSize(width * height, ItemStack.EMPTY);
		this.handler = handler;
		this.width = width;
		this.height = height;
	}

	@Override
	public int getInvSize() {
		return this.stacks.size();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.stacks) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= this.getInvSize() ? ItemStack.EMPTY : this.stacks.get(slot);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return Inventories.removeStack(this.stacks, slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack itemStack = Inventories.splitStack(this.stacks, slot, amount);
		if (!itemStack.isEmpty()) {
			this.handler.onContentChanged(this);
		}

		return itemStack;
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.stacks.set(slot, stack);
		this.handler.onContentChanged(this);
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		this.stacks.clear();
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	public void provideRecipeInputs(RecipeFinder finder) {
		for (ItemStack itemStack : this.stacks) {
			finder.addNormalItem(itemStack);
		}
	}
}
