package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;

public class SliceVoxelShape extends VoxelShape {
	private final VoxelShape shape;
	private final Direction.Axis axis;
	private static final DoubleList points = new FractionalDoubleList(1);

	public SliceVoxelShape(VoxelShape voxelShape, Direction.Axis axis, int i) {
		super(createVoxelSet(voxelShape.voxels, axis, i));
		this.shape = voxelShape;
		this.axis = axis;
	}

	private static VoxelSet createVoxelSet(VoxelSet voxelSet, Direction.Axis axis, int i) {
		return new CroppedVoxelSet(
			voxelSet,
			axis.choose(i, 0, 0),
			axis.choose(0, i, 0),
			axis.choose(0, 0, i),
			axis.choose(i + 1, voxelSet.xSize, voxelSet.xSize),
			axis.choose(voxelSet.ySize, i + 1, voxelSet.ySize),
			axis.choose(voxelSet.zSize, voxelSet.zSize, i + 1)
		);
	}

	@Override
	protected DoubleList getPointPositions(Direction.Axis axis) {
		return axis == this.axis ? points : this.shape.getPointPositions(axis);
	}
}
