package net.minecraft.container;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FurnaceFuelSlot extends Slot {
	public FurnaceFuelSlot(Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
	}

	@Override
	public boolean canInsert(ItemStack itemStack) {
		return FurnaceBlockEntity.canUseAsFuel(itemStack) || isBucket(itemStack);
	}

	@Override
	public int getMaxStackAmount(ItemStack itemStack) {
		return isBucket(itemStack) ? 1 : super.getMaxStackAmount(itemStack);
	}

	public static boolean isBucket(ItemStack itemStack) {
		return itemStack.getItem() == Items.field_8550;
	}
}
