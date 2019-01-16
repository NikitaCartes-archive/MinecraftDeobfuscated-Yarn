package net.minecraft.container;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ShulkerBoxSlot extends Slot {
	public ShulkerBoxSlot(Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
	}

	@Override
	public boolean canInsert(ItemStack itemStack) {
		return !(Block.getBlockFromItem(itemStack.getItem()) instanceof ShulkerBoxBlock);
	}
}
