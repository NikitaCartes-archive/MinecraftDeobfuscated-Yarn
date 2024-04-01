package net.minecraft.screen.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
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
		this.method_59028(stack);
	}

	@Override
	protected void onTake(int amount) {
		this.amount += amount;
	}

	protected void method_59028(ItemStack itemStack) {
		if (this.amount > 0) {
			itemStack.onCraftByPlayer(this.player.getWorld(), this.player, this.amount);
		}

		if (this.inventory instanceof RecipeUnlocker recipeUnlocker) {
			recipeUnlocker.unlockLastRecipe(this.player, this.input.getHeldStacks());
		}

		this.amount = 0;
	}

	@Override
	public void onTakeItem(PlayerEntity player, ItemStack stack) {
		this.method_59028(stack);
		if (player.method_58931("wrote_thoughts", 19) && stack.isOf(Items.POTATO_EYE)) {
			player.method_58932("crafted_eyes");
		}

		DefaultedList<ItemStack> defaultedList = player.getWorld().getRecipeManager().getRemainingStacks(RecipeType.CRAFTING, this.input, player.getWorld());

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = this.input.getStack(i);
			ItemStack itemStack2 = defaultedList.get(i);
			if (!itemStack.isEmpty()) {
				this.input.removeStack(i, 1);
				itemStack = this.input.getStack(i);
			}

			if (!itemStack2.isEmpty()) {
				if (itemStack.isEmpty()) {
					this.input.setStack(i, itemStack2);
				} else if (ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2)) {
					itemStack2.increment(itemStack.getCount());
					this.input.setStack(i, itemStack2);
				} else if (!this.player.getInventory().insertStack(itemStack2)) {
					this.player.dropItem(itemStack2, false);
				}
			}
		}
	}

	@Override
	public boolean disablesDynamicDisplay() {
		return true;
	}
}
