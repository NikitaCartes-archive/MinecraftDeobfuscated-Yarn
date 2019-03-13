package net.minecraft.sortme;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public interface Hopper extends Inventory {
	VoxelShape field_12025 = Block.method_9541(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
	VoxelShape field_12027 = Block.method_9541(0.0, 16.0, 0.0, 16.0, 32.0, 16.0);
	VoxelShape field_12026 = VoxelShapes.method_1084(field_12025, field_12027);

	default VoxelShape method_11262() {
		return field_12026;
	}

	@Nullable
	World getWorld();

	double getHopperX();

	double getHopperY();

	double getHopperZ();
}
