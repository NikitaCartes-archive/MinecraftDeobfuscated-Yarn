package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

public class ArrayVoxelShape extends VoxelShape {
	private final DoubleList xPoints;
	private final DoubleList yPoints;
	private final DoubleList zPoints;

	protected ArrayVoxelShape(VoxelSet shape, double[] xPoints, double[] yPoints, double[] zPoints) {
		this(
			shape,
			DoubleArrayList.wrap(Arrays.copyOf(xPoints, shape.getXSize() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(yPoints, shape.getYSize() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(zPoints, shape.getZSize() + 1))
		);
	}

	ArrayVoxelShape(VoxelSet shape, DoubleList xPoints, DoubleList yPoints, DoubleList zPoints) {
		super(shape);
		int i = shape.getXSize() + 1;
		int j = shape.getYSize() + 1;
		int k = shape.getZSize() + 1;
		if (i == xPoints.size() && j == yPoints.size() && k == zPoints.size()) {
			this.xPoints = xPoints;
			this.yPoints = yPoints;
			this.zPoints = zPoints;
		} else {
			throw (IllegalArgumentException)Util.throwOrPause(
				new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape.")
			);
		}
	}

	@Override
	protected DoubleList getPointPositions(Direction.Axis axis) {
		switch (axis) {
			case X:
				return this.xPoints;
			case Y:
				return this.yPoints;
			case Z:
				return this.zPoints;
			default:
				throw new IllegalArgumentException();
		}
	}
}
