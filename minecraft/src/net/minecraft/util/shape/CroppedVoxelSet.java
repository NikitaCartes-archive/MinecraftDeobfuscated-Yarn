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

	protected CroppedVoxelSet(VoxelSet voxelSet, int i, int j, int k, int l, int m, int n) {
		super(l - i, m - j, n - k);
		this.parent = voxelSet;
		this.xMin = i;
		this.yMin = j;
		this.zMin = k;
		this.xMax = l;
		this.yMax = m;
		this.zMax = n;
	}

	@Override
	public boolean contains(int i, int j, int k) {
		return this.parent.contains(this.xMin + i, this.yMin + j, this.zMin + k);
	}

	@Override
	public void set(int i, int j, int k, boolean bl, boolean bl2) {
		this.parent.set(this.xMin + i, this.yMin + j, this.zMin + k, bl, bl2);
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
