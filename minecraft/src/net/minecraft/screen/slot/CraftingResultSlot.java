package net.minecraft.screen.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.util.DefaultedList;

public class CraftingResultSlot extends Slot {
	private final CraftingInventory craftingInv;
	private final PlayerEntity player;
	private int amount;

	public CraftingResultSlot(PlayerEntity player, CraftingInventory craftingInv, Inventory inventory, int invSlot, int xPosition, int yPosition) {
		super(inventory, invSlot, xPosition, yPosition);
		this.player = player;
		this.craftingInv = craftingInv;
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
			stack.onCraft(this.player.world, this.player, this.amount);
		}

		if (this.inventory instanceof RecipeUnlocker) {
			((RecipeUnlocker)this.inventory).unlockLastRecipe(this.player);
		}

		this.amount = 0;
	}

	@Override
	public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
		this.onCrafted(stack);
		DefaultedList<ItemStack> defaultedList = player.world.getRecipeManager().getRemainingStacks(RecipeType.CRAFTING, this.craftingInv, player.world);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = this.craftingInv.getInvStack(i);
			ItemStack itemStack2 = defaultedList.get(i);
			if (!itemStack.isEmpty()) {
				this.craftingInv.takeInvStack(i, 1);
				itemStack = this.craftingInv.getInvStack(i);
			}

			if (!itemStack2.isEmpty()) {
				if (itemStack.isEmpty()) {
					this.craftingInv.setInvStack(i, itemStack2);
				} else if (ItemStack.areItemsEqualIgnoreDamage(itemStack, itemStack2) && ItemStack.areTagsEqual(itemStack, itemStack2)) {
					itemStack2.increment(itemStack.getCount());
					this.craftingInv.setInvStack(i, itemStack2);
				} else if (!this.player.inventory.insertStack(itemStack2)) {
					this.player.dropItem(itemStack2, false);
				}
			}
		}

		return stack;
	}
}
