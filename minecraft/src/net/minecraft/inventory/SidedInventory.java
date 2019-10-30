package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public interface SidedInventory extends Inventory {
	int[] getInvAvailableSlots(Direction side);

	boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction dir);

	boolean canExtractInvStack(int slot, ItemStack stack, Direction dir);
}
