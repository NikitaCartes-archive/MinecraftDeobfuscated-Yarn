package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

public interface Hopper extends Inventory {
	VoxelShape INSIDE_SHAPE = Block.createCuboidShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
	Box INPUT_AREA_SHAPE = (Box)Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 32.0, 16.0).getBoundingBoxes().get(0);

	default Box getInputAreaShape() {
		return INPUT_AREA_SHAPE;
	}

	double getHopperX();

	double getHopperY();

	double getHopperZ();
}
