package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.util.math.Direction;

final class ArrayVoxelShape extends VoxelShape {
	private final DoubleList xPoints;
	private final DoubleList yPoints;
	private final DoubleList zPoints;

	ArrayVoxelShape(AbstractVoxelShapeContainer abstractVoxelShapeContainer, double[] ds, double[] es, double[] fs) {
		this(
			abstractVoxelShapeContainer,
			DoubleArrayList.wrap(Arrays.copyOf(ds, abstractVoxelShapeContainer.getXSize() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(es, abstractVoxelShapeContainer.getYSize() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(fs, abstractVoxelShapeContainer.getZSize() + 1))
		);
	}

	ArrayVoxelShape(AbstractVoxelShapeContainer abstractVoxelShapeContainer, DoubleList doubleList, DoubleList doubleList2, DoubleList doubleList3) {
		super(abstractVoxelShapeContainer);
		int i = abstractVoxelShapeContainer.getXSize() + 1;
		int j = abstractVoxelShapeContainer.getYSize() + 1;
		int k = abstractVoxelShapeContainer.getZSize() + 1;
		if (i == doubleList.size() && j == doubleList2.size() && k == doubleList3.size()) {
			this.xPoints = doubleList;
			this.yPoints = doubleList2;
			this.zPoints = doubleList3;
		} else {
			throw new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape.");
		}
	}

	@Override
	protected DoubleList getIncludedPoints(Direction.Axis axis) {
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
