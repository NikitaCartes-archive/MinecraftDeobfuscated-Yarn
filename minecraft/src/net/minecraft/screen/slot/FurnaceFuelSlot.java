package net.minecraft.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AbstractFurnaceScreenHandler;

public class FurnaceFuelSlot extends Slot {
	private final AbstractFurnaceScreenHandler handler;

	public FurnaceFuelSlot(AbstractFurnaceScreenHandler handler, Inventory inventory, int invSlot, int xPosition, int yPosition) {
		super(inventory, invSlot, xPosition, yPosition);
		this.handler = handler;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return this.handler.isFuel(stack) || isBucket(stack);
	}

	@Override
	public int getMaxStackAmount(ItemStack itemStack) {
		return isBucket(itemStack) ? 1 : super.getMaxStackAmount(itemStack);
	}

	public static boolean isBucket(ItemStack stack) {
		return stack.getItem() == Items.BUCKET;
	}
}
