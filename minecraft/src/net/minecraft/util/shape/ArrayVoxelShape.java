package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.class_251;
import net.minecraft.util.math.Direction;

final class ArrayVoxelShape extends VoxelShape {
	private final DoubleList xPoints;
	private final DoubleList yPoints;
	private final DoubleList zPoints;

	ArrayVoxelShape(class_251 arg, double[] ds, double[] es, double[] fs) {
		this(
			arg,
			DoubleArrayList.wrap(Arrays.copyOf(ds, arg.method_1050() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(es, arg.method_1047() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(fs, arg.method_1048() + 1))
		);
	}

	ArrayVoxelShape(class_251 arg, DoubleList doubleList, DoubleList doubleList2, DoubleList doubleList3) {
		super(arg);
		int i = arg.method_1050() + 1;
		int j = arg.method_1047() + 1;
		int k = arg.method_1048() + 1;
		if (i == doubleList.size() && j == doubleList2.size() && k == doubleList3.size()) {
			this.xPoints = doubleList;
			this.yPoints = doubleList2;
			this.zPoints = doubleList3;
		} else {
			throw new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape.");
		}
	}

	@Override
	protected DoubleList method_1109(Direction.Axis axis) {
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
