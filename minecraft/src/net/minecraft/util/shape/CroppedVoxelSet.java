package net.minecraft.util.shape;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

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
	public void set(int x, int y, int z) {
		this.parent.set(this.xMin + x, this.yMin + y, this.zMin + z);
	}

	@Override
	public int getMin(Direction.Axis axis) {
		return this.clamp(axis, this.parent.getMin(axis));
	}

	@Override
	public int getMax(Direction.Axis axis) {
		return this.clamp(axis, this.parent.getMax(axis));
	}

	private int clamp(Direction.Axis axis, int value) {
		int i = axis.choose(this.xMin, this.yMin, this.zMin);
		int j = axis.choose(this.xMax, this.yMax, this.zMax);
		return MathHelper.clamp(value, i, j) - i;
	}
}
