package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

final class SimpleVoxelShape extends VoxelShape {
	SimpleVoxelShape(AbstractVoxelShapeContainer abstractVoxelShapeContainer) {
		super(abstractVoxelShapeContainer);
	}

	@Override
	protected DoubleList getIncludedPoints(Direction.Axis axis) {
		return new FractionalDoubleList(this.shape.getSize(axis));
	}

	@Override
	protected int method_1100(Direction.Axis axis, double d) {
		int i = this.shape.getSize(axis);
		return MathHelper.clamp(MathHelper.floor(d * (double)i), -1, i);
	}
}
