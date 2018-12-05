package net.minecraft.sortme;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public interface Hopper extends Inventory {
	VoxelShape SHAPE_INSIDE = Block.createCubeShape(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
	VoxelShape SHAPE_ABOVE = Block.createCubeShape(0.0, 16.0, 0.0, 16.0, 32.0, 16.0);
	VoxelShape SHAPE_INPUT = VoxelShapes.method_1084(SHAPE_INSIDE, SHAPE_ABOVE);

	default VoxelShape getInputAreaShape() {
		return SHAPE_INPUT;
	}

	@Nullable
	World getWorld();

	double getHopperX();

	double getHopperY();

	double getHopperZ();
}
