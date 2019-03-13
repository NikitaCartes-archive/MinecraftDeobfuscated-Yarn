package net.minecraft.container;

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
	private int field_7869;

	public CraftingResultSlot(PlayerEntity playerEntity, CraftingInventory craftingInventory, Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
		this.player = playerEntity;
		this.craftingInv = craftingInventory;
	}

	@Override
	public boolean method_7680(ItemStack itemStack) {
		return false;
	}

	@Override
	public ItemStack method_7671(int i) {
		if (this.hasStack()) {
			this.field_7869 = this.field_7869 + Math.min(i, this.method_7677().getAmount());
		}

		return super.method_7671(i);
	}

	@Override
	protected void method_7678(ItemStack itemStack, int i) {
		this.field_7869 += i;
		this.method_7669(itemStack);
	}

	@Override
	protected void onTake(int i) {
		this.field_7869 += i;
	}

	@Override
	protected void method_7669(ItemStack itemStack) {
		if (this.field_7869 > 0) {
			itemStack.method_7982(this.player.field_6002, this.player, this.field_7869);
		}

		if (this.inventory instanceof RecipeUnlocker) {
			((RecipeUnlocker)this.inventory).unlockLastRecipe(this.player);
		}

		this.field_7869 = 0;
	}

	@Override
	public ItemStack method_7667(PlayerEntity playerEntity, ItemStack itemStack) {
		this.method_7669(itemStack);
		DefaultedList<ItemStack> defaultedList = playerEntity.field_6002
			.getRecipeManager()
			.method_8128(RecipeType.CRAFTING, this.craftingInv, playerEntity.field_6002);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack2 = this.craftingInv.method_5438(i);
			ItemStack itemStack3 = defaultedList.get(i);
			if (!itemStack2.isEmpty()) {
				this.craftingInv.method_5434(i, 1);
				itemStack2 = this.craftingInv.method_5438(i);
			}

			if (!itemStack3.isEmpty()) {
				if (itemStack2.isEmpty()) {
					this.craftingInv.method_5447(i, itemStack3);
				} else if (ItemStack.areEqualIgnoreTags(itemStack2, itemStack3) && ItemStack.areTagsEqual(itemStack2, itemStack3)) {
					itemStack3.addAmount(itemStack2.getAmount());
					this.craftingInv.method_5447(i, itemStack3);
				} else if (!this.player.inventory.method_7394(itemStack3)) {
					this.player.method_7328(itemStack3, false);
				}
			}
		}

		return itemStack;
	}
}
