package net.minecraft.block.dispenser;

import net.minecraft.util.math.BlockPointer;

public abstract class FallibleItemDispenserBehavior extends ItemDispenserBehavior {
	protected boolean success = true;

	@Override
	protected void playSound(BlockPointer blockPointer) {
		blockPointer.getWorld().method_20290(this.success ? 1000 : 1001, blockPointer.getBlockPos(), 0);
	}
}
