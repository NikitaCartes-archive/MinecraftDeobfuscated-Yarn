package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.util.math.Direction;

public final class ArrayVoxelShape extends VoxelShape {
	private final DoubleList xPoints;
	private final DoubleList yPoints;
	private final DoubleList zPoints;

	protected ArrayVoxelShape(VoxelSet voxelSet, double[] ds, double[] es, double[] fs) {
		this(
			voxelSet,
			DoubleArrayList.wrap(Arrays.copyOf(ds, voxelSet.getXSize() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(es, voxelSet.getYSize() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(fs, voxelSet.getZSize() + 1))
		);
	}

	ArrayVoxelShape(VoxelSet voxelSet, DoubleList doubleList, DoubleList doubleList2, DoubleList doubleList3) {
		super(voxelSet);
		int i = voxelSet.getXSize() + 1;
		int j = voxelSet.getYSize() + 1;
		int k = voxelSet.getZSize() + 1;
		if (i == doubleList.size() && j == doubleList2.size() && k == doubleList3.size()) {
			this.xPoints = doubleList;
			this.yPoints = doubleList2;
			this.zPoints = doubleList3;
		} else {
			throw new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape.");
		}
	}

	@Override
	protected DoubleList getPointPositions(Direction.Axis axis) {
		switch (axis) {
			case field_11048:
				return this.xPoints;
			case field_11052:
				return this.yPoints;
			case field_11051:
				return this.zPoints;
			default:
				throw new IllegalArgumentException();
		}
	}
}
