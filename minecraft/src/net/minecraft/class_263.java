package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.AbstractVoxelShapeContainer;
import net.minecraft.util.shape.FractionalDoubleList;
import net.minecraft.util.shape.OffsetVoxelShapeContainer;
import net.minecraft.util.shape.VoxelShape;

public class class_263 extends VoxelShape {
	private final VoxelShape parent;
	private final Direction.Axis axis;
	private final DoubleList axisList = new FractionalDoubleList(1);

	public class_263(VoxelShape voxelShape, Direction.Axis axis, int i) {
		super(offset(voxelShape.shape, axis, i));
		this.parent = voxelShape;
		this.axis = axis;
	}

	private static AbstractVoxelShapeContainer offset(AbstractVoxelShapeContainer abstractVoxelShapeContainer, Direction.Axis axis, int i) {
		return new OffsetVoxelShapeContainer(
			abstractVoxelShapeContainer,
			axis.choose(i, 0, 0),
			axis.choose(0, i, 0),
			axis.choose(0, 0, i),
			axis.choose(i + 1, abstractVoxelShapeContainer.xSize, abstractVoxelShapeContainer.xSize),
			axis.choose(abstractVoxelShapeContainer.ySize, i + 1, abstractVoxelShapeContainer.ySize),
			axis.choose(abstractVoxelShapeContainer.zSize, abstractVoxelShapeContainer.zSize, i + 1)
		);
	}

	@Override
	protected DoubleList getIncludedPoints(Direction.Axis axis) {
		return axis == this.axis ? this.axisList : this.parent.getIncludedPoints(axis);
	}
}
