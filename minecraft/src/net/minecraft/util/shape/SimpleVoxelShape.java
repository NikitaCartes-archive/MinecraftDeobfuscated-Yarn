package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

final class SimpleVoxelShape extends VoxelShape {
	SimpleVoxelShape(VoxelSet voxelSet) {
		super(voxelSet);
	}

	@Override
	protected DoubleList getIncludedPoints(Direction.Axis axis) {
		return new FractionalDoubleList(this.voxels.getSize(axis));
	}

	@Override
	protected int getCoordIndex(Direction.Axis axis, double d) {
		int i = this.voxels.getSize(axis);
		return MathHelper.clamp(MathHelper.floor(d * (double)i), -1, i);
	}
}
