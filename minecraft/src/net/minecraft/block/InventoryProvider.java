package net.minecraft.block;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface InventoryProvider {
	SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos);
}
