package net.minecraft.util.shape;

import net.minecraft.util.math.Direction;

public final class CroppedVoxelSet extends VoxelSet {
	private final VoxelSet parent;
	private final int xMin;
	private final int yMin;
	private final int zMin;
	private final int xMax;
	private final int yMax;
	private final int zMax;

	protected CroppedVoxelSet(VoxelSet parent, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
		super(xMax - xMin, yMax - yMin, zMax - zMin);
		this.parent = parent;
		this.xMin = xMin;
		this.yMin = yMin;
		this.zMin = zMin;
		this.xMax = xMax;
		this.yMax = yMax;
		this.zMax = zMax;
	}

	@Override
	public boolean contains(int x, int y, int z) {
		return this.parent.contains(this.xMin + x, this.yMin + y, this.zMin + z);
	}

	@Override
	public void set(int x, int y, int z, boolean resize, boolean included) {
		this.parent.set(this.xMin + x, this.yMin + y, this.zMin + z, resize, included);
	}

	@Override
	public int getMin(Direction.Axis axis) {
		return Math.max(0, this.parent.getMin(axis) - axis.choose(this.xMin, this.yMin, this.zMin));
	}

	@Override
	public int getMax(Direction.Axis axis) {
		return Math.min(axis.choose(this.xMax, this.yMax, this.zMax), this.parent.getMax(axis) - axis.choose(this.xMin, this.yMin, this.zMin));
	}
}
