package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public interface SidedInventory extends Inventory {
	int[] getInvAvailableSlots(Direction direction);

	boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction);

	boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction);
}
