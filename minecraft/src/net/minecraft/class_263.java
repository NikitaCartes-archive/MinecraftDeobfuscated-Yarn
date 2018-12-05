package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.FractionalDoubleList;
import net.minecraft.util.shape.OffsetVoxelShapeContainer;
import net.minecraft.util.shape.VoxelShape;

public class class_263 extends VoxelShape {
	private final VoxelShape parent;
	private final Direction.Axis field_1396;
	private final DoubleList axisList = new FractionalDoubleList(1);

	public class_263(VoxelShape voxelShape, Direction.Axis axis, int i) {
		super(method_1088(voxelShape.shape, axis, i));
		this.parent = voxelShape;
		this.field_1396 = axis;
	}

	private static class_251 method_1088(class_251 arg, Direction.Axis axis, int i) {
		return new OffsetVoxelShapeContainer(
			arg,
			axis.choose(i, 0, 0),
			axis.choose(0, i, 0),
			axis.choose(0, 0, i),
			axis.choose(i + 1, arg.field_1374, arg.field_1374),
			axis.choose(arg.field_1373, i + 1, arg.field_1373),
			axis.choose(arg.field_1372, arg.field_1372, i + 1)
		);
	}

	@Override
	protected DoubleList method_1109(Direction.Axis axis) {
		return axis == this.field_1396 ? this.axisList : this.parent.method_1109(axis);
	}
}
