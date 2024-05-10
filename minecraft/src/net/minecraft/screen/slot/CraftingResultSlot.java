package net.minecraft.screen.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.util.collection.DefaultedList;

public class CraftingResultSlot extends Slot {
	private final RecipeInputInventory input;
	private final PlayerEntity player;
	private int amount;

	public CraftingResultSlot(PlayerEntity player, RecipeInputInventory input, Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		this.player = player;
		this.input = input;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int amount) {
		if (this.hasStack()) {
			this.amount = this.amount + Math.min(amount, this.getStack().getCount());
		}

		return super.takeStack(amount);
	}

	@Override
	protected void onCrafted(ItemStack stack, int amount) {
		this.amount += amount;
		this.onCrafted(stack);
	}

	@Override
	protected void onTake(int amount) {
		this.amount += amount;
	}

	@Override
	protected void onCrafted(ItemStack stack) {
		if (this.amount > 0) {
			stack.onCraftByPlayer(this.player.getWorld(), this.player, this.amount);
		}

		if (this.inventory instanceof RecipeUnlocker recipeUnlocker) {
			recipeUnlocker.unlockLastRecipe(this.player, this.input.getHeldStacks());
		}

		this.amount = 0;
	}

	@Override
	public void onTakeItem(PlayerEntity player, ItemStack stack) {
		this.onCrafted(stack);
		CraftingRecipeInput.Positioned positioned = this.input.createPositionedRecipeInput();
		CraftingRecipeInput craftingRecipeInput = positioned.input();
		int i = positioned.left();
		int j = positioned.top();
		DefaultedList<ItemStack> defaultedList = player.getWorld().getRecipeManager().getRemainingStacks(RecipeType.CRAFTING, craftingRecipeInput, player.getWorld());

		for (int k = 0; k < craftingRecipeInput.getHeight(); k++) {
			for (int l = 0; l < craftingRecipeInput.getWidth(); l++) {
				int m = l + i + (k + j) * this.input.getWidth();
				ItemStack itemStack = this.input.getStack(m);
				ItemStack itemStack2 = defaultedList.get(l + k * craftingRecipeInput.getWidth());
				if (!itemStack.isEmpty()) {
					this.input.removeStack(m, 1);
					itemStack = this.input.getStack(m);
				}

				if (!itemStack2.isEmpty()) {
					if (itemStack.isEmpty()) {
						this.input.setStack(m, itemStack2);
					} else if (ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2)) {
						itemStack2.increment(itemStack.getCount());
						this.input.setStack(m, itemStack2);
					} else if (!this.player.getInventory().insertStack(itemStack2)) {
						this.player.dropItem(itemStack2, false);
					}
				}
			}
		}
	}

	@Override
	public boolean disablesDynamicDisplay() {
		return true;
	}
}
