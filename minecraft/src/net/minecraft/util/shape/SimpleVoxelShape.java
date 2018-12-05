package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.class_251;
import net.minecraft.util.math.Direction;

final class SimpleVoxelShape extends VoxelShape {
	SimpleVoxelShape(class_251 arg) {
		super(arg);
	}

	@Override
	protected DoubleList method_1109(Direction.Axis axis) {
		return new FractionalDoubleList(this.shape.method_1051(axis));
	}
}
