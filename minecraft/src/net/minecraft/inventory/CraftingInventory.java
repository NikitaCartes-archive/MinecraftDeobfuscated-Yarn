package net.minecraft.inventory;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

public class CraftingInventory implements RecipeInputInventory {
	private final DefaultedList<ItemStack> stacks;
	private final int width;
	private final int height;
	private final ScreenHandler handler;

	public CraftingInventory(ScreenHandler handler, int width, int height) {
		this(handler, width, height, DefaultedList.ofSize(width * height, ItemStack.EMPTY));
	}

	public CraftingInventory(ScreenHandler handler, int width, int height, DefaultedList<ItemStack> stacks) {
		this.stacks = stacks;
		this.handler = handler;
		this.width = width;
		this.height = height;
	}

	@Override
	public int size() {
		return this.stacks.size();
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
	public ItemStack getStack(int slot) {
		return slot >= this.size() ? ItemStack.EMPTY : this.stacks.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(this.stacks, slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack itemStack = Inventories.splitStack(this.stacks, slot, amount);
		if (!itemStack.isEmpty()) {
			this.handler.onContentChanged(this);
		}

		return itemStack;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.stacks.set(slot, stack);
		this.handler.onContentChanged(this);
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		this.stacks.clear();
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public List<ItemStack> getInputStacks() {
		return List.copyOf(this.stacks);
	}

	@Override
	public void provideRecipeInputs(RecipeMatcher finder) {
		for (ItemStack itemStack : this.stacks) {
			finder.addUnenchantedInput(itemStack);
		}
	}
}
