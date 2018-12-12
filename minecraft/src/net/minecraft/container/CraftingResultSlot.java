package net.minecraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.util.DefaultedList;

public class CraftingResultSlot extends Slot {
	private final CraftingInventory craftingInv;
	private final PlayerEntity player;
	private int field_7869;

	public CraftingResultSlot(PlayerEntity playerEntity, CraftingInventory craftingInventory, Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
		this.player = playerEntity;
		this.craftingInv = craftingInventory;
	}

	@Override
	public boolean canInsert(ItemStack itemStack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int i) {
		if (this.hasStack()) {
			this.field_7869 = this.field_7869 + Math.min(i, this.getStack().getAmount());
		}

		return super.takeStack(i);
	}

	@Override
	protected void onCrafted(ItemStack itemStack, int i) {
		this.field_7869 += i;
		this.onCrafted(itemStack);
	}

	@Override
	protected void method_7672(int i) {
		this.field_7869 += i;
	}

	@Override
	protected void onCrafted(ItemStack itemStack) {
		if (this.field_7869 > 0) {
			itemStack.onCrafted(this.player.world, this.player, this.field_7869);
		}

		((RecipeUnlocker)this.inventory).unlockLastRecipe(this.player);
		this.field_7869 = 0;
	}

	@Override
	public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
		this.onCrafted(itemStack);
		DefaultedList<ItemStack> defaultedList = playerEntity.world.getRecipeManager().method_8128(this.craftingInv, playerEntity.world);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack2 = this.craftingInv.getInvStack(i);
			ItemStack itemStack3 = defaultedList.get(i);
			if (!itemStack2.isEmpty()) {
				this.craftingInv.takeInvStack(i, 1);
				itemStack2 = this.craftingInv.getInvStack(i);
			}

			if (!itemStack3.isEmpty()) {
				if (itemStack2.isEmpty()) {
					this.craftingInv.setInvStack(i, itemStack3);
				} else if (ItemStack.areEqualIgnoreTags(itemStack2, itemStack3) && ItemStack.areTagsEqual(itemStack2, itemStack3)) {
					itemStack3.addAmount(itemStack2.getAmount());
					this.craftingInv.setInvStack(i, itemStack3);
				} else if (!this.player.inventory.insertStack(itemStack3)) {
					this.player.dropItem(itemStack3, false);
				}
			}
		}

		return itemStack;
	}
}
