package net.minecraft.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ShulkerBoxSlot extends Slot {
	public ShulkerBoxSlot(Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return stack.getItem().canBeNested();
	}
}
