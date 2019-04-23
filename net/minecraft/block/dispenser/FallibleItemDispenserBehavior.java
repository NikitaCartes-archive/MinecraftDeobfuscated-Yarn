/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.util.math.BlockPointer;

public abstract class FallibleItemDispenserBehavior
extends ItemDispenserBehavior {
    protected boolean success = true;

    @Override
    protected void playSound(BlockPointer blockPointer) {
        blockPointer.getWorld().playLevelEvent(this.success ? 1000 : 1001, blockPointer.getBlockPos(), 0);
    }
}

