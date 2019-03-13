package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public interface SidedInventory extends Inventory {
	int[] method_5494(Direction direction);

	boolean method_5492(int i, ItemStack itemStack, @Nullable Direction direction);

	boolean method_5493(int i, ItemStack itemStack, Direction direction);
}
