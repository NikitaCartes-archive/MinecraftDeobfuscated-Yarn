package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.IntArrayTag;

public class BlockBox {
	public int minX;
	public int minY;
	public int minZ;
	public int maxX;
	public int maxY;
	public int maxZ;

	public BlockBox() {
	}

	public BlockBox(int[] data) {
		if (data.length == 6) {
			this.minX = data[0];
			this.minY = data[1];
			this.minZ = data[2];
			this.maxX = data[3];
			this.maxY = data[4];
			this.maxZ = data[5];
		}
	}

	public static BlockBox empty() {
		return new BlockBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	public static BlockBox infinite() {
		return new BlockBox(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public static BlockBox rotated(int x, int y, int z, int offsetX, int offsetY, int offsetZ, int sizeX, int sizeY, int sizeZ, Direction facing) {
		switch (facing) {
			case NORTH:
				return new BlockBox(x + offsetX, y + offsetY, z - sizeZ + 1 + offsetZ, x + sizeX - 1 + offsetX, y + sizeY - 1 + offsetY, z + offsetZ);
			case SOUTH:
				return new BlockBox(x + offsetX, y + offsetY, z + offsetZ, x + sizeX - 1 + offsetX, y + sizeY - 1 + offsetY, z + sizeZ - 1 + offsetZ);
			case WEST:
				return new BlockBox(x - sizeZ + 1 + offsetZ, y + offsetY, z + offsetX, x + offsetZ, y + sizeY - 1 + offsetY, z + sizeX - 1 + offsetX);
			case EAST:
				return new BlockBox(x + offsetZ, y + offsetY, z + offsetX, x + sizeZ - 1 + offsetZ, y + sizeY - 1 + offsetY, z + sizeX - 1 + offsetX);
			default:
				return new BlockBox(x + offsetX, y + offsetY, z + offsetZ, x + sizeX - 1 + offsetX, y + sizeY - 1 + offsetY, z + sizeZ - 1 + offsetZ);
		}
	}

	public static BlockBox create(int x1, int y1, int z1, int x2, int y2, int z2) {
		return new BlockBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
	}

	public BlockBox(BlockBox source) {
		this.minX = source.minX;
		this.minY = source.minY;
		this.minZ = source.minZ;
		this.maxX = source.maxX;
		this.maxY = source.maxY;
		this.maxZ = source.maxZ;
	}

	public BlockBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	public BlockBox(Vec3i v1, Vec3i v2) {
		this.minX = Math.min(v1.getX(), v2.getX());
		this.minY = Math.min(v1.getY(), v2.getY());
		this.minZ = Math.min(v1.getZ(), v2.getZ());
		this.maxX = Math.max(v1.getX(), v2.getX());
		this.maxY = Math.max(v1.getY(), v2.getY());
		this.maxZ = Math.max(v1.getZ(), v2.getZ());
	}

	public BlockBox(int minX, int minZ, int maxX, int maxZ) {
		this.minX = minX;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxZ = maxZ;
		this.minY = 1;
		this.maxY = 512;
	}

	public boolean intersects(BlockBox other) {
		return this.maxX >= other.minX
			&& this.minX <= other.maxX
			&& this.maxZ >= other.minZ
			&& this.minZ <= other.maxZ
			&& this.maxY >= other.minY
			&& this.minY <= other.maxY;
	}

	public boolean intersectsXZ(int minX, int minZ, int maxX, int maxZ) {
		return this.maxX >= minX && this.minX <= maxX && this.maxZ >= minZ && this.minZ <= maxZ;
	}

	public void encompass(BlockBox region) {
		this.minX = Math.min(this.minX, region.minX);
		this.minY = Math.min(this.minY, region.minY);
		this.minZ = Math.min(this.minZ, region.minZ);
		this.maxX = Math.max(this.maxX, region.maxX);
		this.maxY = Math.max(this.maxY, region.maxY);
		this.maxZ = Math.max(this.maxZ, region.maxZ);
	}

	public void move(int dx, int dy, int dz) {
		this.minX += dx;
		this.minY += dy;
		this.minZ += dz;
		this.maxX += dx;
		this.maxY += dy;
		this.maxZ += dz;
	}

	public BlockBox offset(int x, int y, int z) {
		return new BlockBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}

	public void move(Vec3i vec3i) {
		this.move(vec3i.getX(), vec3i.getY(), vec3i.getZ());
	}

	public boolean contains(Vec3i vec) {
		return vec.getX() >= this.minX
			&& vec.getX() <= this.maxX
			&& vec.getZ() >= this.minZ
			&& vec.getZ() <= this.maxZ
			&& vec.getY() >= this.minY
			&& vec.getY() <= this.maxY;
	}

	public Vec3i getDimensions() {
		return new Vec3i(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
	}

	public int getBlockCountX() {
		return this.maxX - this.minX + 1;
	}

	public int getBlockCountY() {
		return this.maxY - this.minY + 1;
	}

	public int getBlockCountZ() {
		return this.maxZ - this.minZ + 1;
	}

	/**
	 * @implNote Biased toward the minimum bound corner of the box.
	 */
	public Vec3i getCenter() {
		return new BlockPos(this.minX + (this.maxX - this.minX + 1) / 2, this.minY + (this.maxY - this.minY + 1) / 2, this.minZ + (this.maxZ - this.minZ + 1) / 2);
	}

	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("x0", this.minX)
			.add("y0", this.minY)
			.add("z0", this.minZ)
			.add("x1", this.maxX)
			.add("y1", this.maxY)
			.add("z1", this.maxZ)
			.toString();
	}

	public IntArrayTag toNbt() {
		return new IntArrayTag(new int[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
	}
}
