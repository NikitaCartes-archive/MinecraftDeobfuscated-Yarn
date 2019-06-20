package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;

public final class class_245 extends class_265 {
	private final DoubleList field_1361;
	private final DoubleList field_1362;
	private final DoubleList field_1363;

	protected class_245(class_251 arg, double[] ds, double[] es, double[] fs) {
		this(
			arg,
			DoubleArrayList.wrap(Arrays.copyOf(ds, arg.method_1050() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(es, arg.method_1047() + 1)),
			DoubleArrayList.wrap(Arrays.copyOf(fs, arg.method_1048() + 1))
		);
	}

	class_245(class_251 arg, DoubleList doubleList, DoubleList doubleList2, DoubleList doubleList3) {
		super(arg);
		int i = arg.method_1050() + 1;
		int j = arg.method_1047() + 1;
		int k = arg.method_1048() + 1;
		if (i == doubleList.size() && j == doubleList2.size() && k == doubleList3.size()) {
			this.field_1361 = doubleList;
			this.field_1362 = doubleList2;
			this.field_1363 = doubleList3;
		} else {
			throw new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape.");
		}
	}

	@Override
	protected DoubleList method_1109(class_2350.class_2351 arg) {
		switch (arg) {
			case field_11048:
				return this.field_1361;
			case field_11052:
				return this.field_1362;
			case field_11051:
				return this.field_1363;
			default:
				throw new IllegalArgumentException();
		}
	}
}
