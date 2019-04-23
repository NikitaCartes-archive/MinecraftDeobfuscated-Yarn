/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface InventoryProvider {
    public SidedInventory getInventory(BlockState var1, IWorld var2, BlockPos var3);
}

