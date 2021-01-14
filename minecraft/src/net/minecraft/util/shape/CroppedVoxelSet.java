package net.minecraft.util.shape;

import net.minecraft.util.math.Direction;

public final class CroppedVoxelSet extends VoxelSet {
	private final VoxelSet parent;
	private final int minX;
	private final int minY;
	private final int minZ;
	private final int maxX;
	private final int maxY;
	private final int maxZ;

	protected CroppedVoxelSet(VoxelSet parent, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		super(maxX - minX, maxY - minY, maxZ - minZ);
		this.parent = parent;
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	@Override
	public boolean contains(int x, int y, int z) {
		return this.parent.contains(this.minX + x, this.minY + y, this.minZ + z);
	}

	@Override
	public void set(int x, int y, int z, boolean resize, boolean included) {
		this.parent.set(this.minX + x, this.minY + y, this.minZ + z, resize, included);
	}

	@Override
	public int getMin(Direction.Axis axis) {
		return Math.max(0, this.parent.getMin(axis) - axis.choose(this.minX, this.minY, this.minZ));
	}

	@Override
	public int getMax(Direction.Axis axis) {
		return Math.min(axis.choose(this.maxX, this.maxY, this.maxZ), this.parent.getMax(axis) - axis.choose(this.minX, this.minY, this.minZ));
	}
}
