package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

/**
 * A special inventory interface for inventories that expose different slots for different sides, such as furnaces.
 */
public interface SidedInventory extends Inventory {
	/**
	 * {@return the available slot positions that are reachable from a given side}
	 */
	int[] getAvailableSlots(Direction side);

	/**
	 * {@return whether the given stack can be inserted into this inventory
	 * at the specified slot position from the given direction}
	 */
	boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir);

	/**
	 * {@return whether the given stack can be removed from this inventory at the
	 * specified slot position from the given direction}
	 */
	boolean canExtract(int slot, ItemStack stack, Direction dir);
}
