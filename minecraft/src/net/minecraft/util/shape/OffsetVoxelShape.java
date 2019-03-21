package net.minecraft.util.shape;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;

public final class OffsetVoxelShape extends VoxelShape {
	private final int xOffset;
	private final int yOffset;
	private final int zOffset;

	public OffsetVoxelShape(VoxelSet voxelSet, int i, int j, int k) {
		super(voxelSet);
		this.xOffset = i;
		this.yOffset = j;
		this.zOffset = k;
	}

	@Override
	protected DoubleList getIncludedPoints(Direction.Axis axis) {
		return new OffsetFractionalDoubleList(this.voxels.getSize(axis), axis.choose(this.xOffset, this.yOffset, this.zOffset));
	}
}
