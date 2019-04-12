package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public interface Hopper extends Inventory {
	VoxelShape INSIDE_SHAPE = Block.createCuboidShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
	VoxelShape ABOVE_SHAPE = Block.createCuboidShape(0.0, 16.0, 0.0, 16.0, 32.0, 16.0);
	VoxelShape INPUT_AREA_SHAPE = VoxelShapes.union(INSIDE_SHAPE, ABOVE_SHAPE);

	default VoxelShape getInputAreaShape() {
		return INPUT_AREA_SHAPE;
	}

	@Nullable
	World getWorld();

	double getHopperX();

	double getHopperY();

	double getHopperZ();
}
