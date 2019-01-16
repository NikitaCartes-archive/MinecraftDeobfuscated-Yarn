package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;

final class SimpleVoxelShape extends VoxelShape {
	SimpleVoxelShape(AbstractVoxelShapeContainer abstractVoxelShapeContainer) {
		super(abstractVoxelShapeContainer);
	}

	@Override
	protected DoubleList getIncludedPoints(Direction.Axis axis) {
		return new FractionalDoubleList(this.shape.getSize(axis));
	}
}
