package net.minecraft;

import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.util.math.BlockPointer;

public abstract class class_2969 extends ItemDispenserBehavior {
	protected boolean field_13364 = true;

	@Override
	protected void playSound(BlockPointer blockPointer) {
		blockPointer.getWorld().playEvent(this.field_13364 ? 1000 : 1001, blockPointer.getBlockPos(), 0);
	}
}
