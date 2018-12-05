package net.minecraft.block.dispenser;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;

public interface DispenserBehavior {
	DispenserBehavior NOOP = (blockPointer, itemStack) -> itemStack;

	ItemStack dispense(BlockPointer blockPointer, ItemStack itemStack);
}
