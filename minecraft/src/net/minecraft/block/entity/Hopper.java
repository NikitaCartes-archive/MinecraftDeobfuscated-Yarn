package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Box;

public interface Hopper extends Inventory {
	Box INPUT_AREA_SHAPE = (Box)Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 32.0, 16.0).getBoundingBoxes().get(0);

	default Box getInputAreaShape() {
		return INPUT_AREA_SHAPE;
	}

	double getHopperX();

	double getHopperY();

	double getHopperZ();

	boolean canBlockFromAbove();
}
