package net.minecraft.container;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ShulkerBoxSlot extends Slot {
	public ShulkerBoxSlot(Inventory inventory, int invSlot, int xPosition, int i) {
		super(inventory, invSlot, xPosition, i);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
	}
}
