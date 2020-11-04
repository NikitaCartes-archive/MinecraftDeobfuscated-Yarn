package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public final class SimpleVoxelShape extends VoxelShape {
	protected SimpleVoxelShape(VoxelSet voxelSet) {
		super(voxelSet);
	}

	@Override
	protected DoubleList getPointPositions(Direction.Axis axis) {
		return new FractionalDoubleList(this.voxels.getSize(axis));
	}

	@Override
	protected int getCoordIndex(Direction.Axis axis, double coord) {
		int i = this.voxels.getSize(axis);
		return MathHelper.floor(MathHelper.clamp(coord * (double)i, -1.0, (double)i));
	}
}
