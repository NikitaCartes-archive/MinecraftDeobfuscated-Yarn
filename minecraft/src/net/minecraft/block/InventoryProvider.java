package net.minecraft.block;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface InventoryProvider {
	SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos);
}
