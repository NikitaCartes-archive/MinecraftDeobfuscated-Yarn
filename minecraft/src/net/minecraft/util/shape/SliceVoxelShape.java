package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;

public class SliceVoxelShape extends VoxelShape {
	private final VoxelShape shape;
	private final Direction.Axis axis;
	private static final DoubleList POINTS = new FractionalDoubleList(1);

	public SliceVoxelShape(VoxelShape shape, Direction.Axis axis, int voxelIndex) {
		super(createVoxelSet(shape.voxels, axis, voxelIndex));
		this.shape = shape;
		this.axis = axis;
	}

	private static VoxelSet createVoxelSet(VoxelSet voxels, Direction.Axis axis, int i) {
		return new CroppedVoxelSet(
			voxels,
			axis.choose(i, 0, 0),
			axis.choose(0, i, 0),
			axis.choose(0, 0, i),
			axis.choose(i + 1, voxels.xSize, voxels.xSize),
			axis.choose(voxels.ySize, i + 1, voxels.ySize),
			axis.choose(voxels.zSize, voxels.zSize, i + 1)
		);
	}

	@Override
	protected DoubleList getPointPositions(Direction.Axis axis) {
		return axis == this.axis ? POINTS : this.shape.getPointPositions(axis);
	}
}
