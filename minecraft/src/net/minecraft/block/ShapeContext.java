package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public interface ShapeContext {
	static ShapeContext absent() {
		return EntityShapeContext.ABSENT;
	}

	static ShapeContext of(Entity entity) {
		return new EntityShapeContext(entity);
	}

	boolean isDescending();

	boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue);

	boolean isWearingOnFeet(Item item);

	boolean isHolding(Item item);

	boolean canWalkOnFluid(FluidState state, FlowableFluid fluid);
}
