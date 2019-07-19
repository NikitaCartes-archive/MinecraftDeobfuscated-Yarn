package net.minecraft.container;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FurnaceFuelSlot extends Slot {
	private final AbstractFurnaceContainer container;

	public FurnaceFuelSlot(AbstractFurnaceContainer abstractFurnaceContainer, Inventory inventory, int invSlot, int xPosition, int yPosition) {
		super(inventory, invSlot, xPosition, yPosition);
		this.container = abstractFurnaceContainer;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return this.container.isFuel(stack) || isBucket(stack);
	}

	@Override
	public int getMaxStackAmount(ItemStack itemStack) {
		return isBucket(itemStack) ? 1 : super.getMaxStackAmount(itemStack);
	}

	public static boolean isBucket(ItemStack stack) {
		return stack.getItem() == Items.BUCKET;
	}
}
