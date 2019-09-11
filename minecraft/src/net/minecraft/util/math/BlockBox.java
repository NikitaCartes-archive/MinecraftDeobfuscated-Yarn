package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

	public BlockBox(int[] is) {
		if (is.length == 6) {
			this.minX = is[0];
			this.minY = is[1];
			this.minZ = is[2];
			this.maxX = is[3];
			this.maxY = is[4];
			this.maxZ = is[5];
		}
	}

	public static BlockBox empty() {
		return new BlockBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	public static BlockBox rotated(int i, int j, int k, int l, int m, int n, int o, int p, int q, Direction direction) {
		switch (direction) {
			case NORTH:
				return new BlockBox(i + l, j + m, k - q + 1 + n, i + o - 1 + l, j + p - 1 + m, k + n);
			case SOUTH:
				return new BlockBox(i + l, j + m, k + n, i + o - 1 + l, j + p - 1 + m, k + q - 1 + n);
			case WEST:
				return new BlockBox(i - q + 1 + n, j + m, k + l, i + n, j + p - 1 + m, k + o - 1 + l);
			case EAST:
				return new BlockBox(i + n, j + m, k + l, i + q - 1 + n, j + p - 1 + m, k + o - 1 + l);
			default:
				return new BlockBox(i + l, j + m, k + n, i + o - 1 + l, j + p - 1 + m, k + q - 1 + n);
		}
	}

	public static BlockBox create(int i, int j, int k, int l, int m, int n) {
		return new BlockBox(Math.min(i, l), Math.min(j, m), Math.min(k, n), Math.max(i, l), Math.max(j, m), Math.max(k, n));
	}

	public BlockBox(BlockBox blockBox) {
		this.minX = blockBox.minX;
		this.minY = blockBox.minY;
		this.minZ = blockBox.minZ;
		this.maxX = blockBox.maxX;
		this.maxY = blockBox.maxY;
		this.maxZ = blockBox.maxZ;
	}

	public BlockBox(int i, int j, int k, int l, int m, int n) {
		this.minX = i;
		this.minY = j;
		this.minZ = k;
		this.maxX = l;
		this.maxY = m;
		this.maxZ = n;
	}

	public BlockBox(Vec3i vec3i, Vec3i vec3i2) {
		this.minX = Math.min(vec3i.getX(), vec3i2.getX());
		this.minY = Math.min(vec3i.getY(), vec3i2.getY());
		this.minZ = Math.min(vec3i.getZ(), vec3i2.getZ());
		this.maxX = Math.max(vec3i.getX(), vec3i2.getX());
		this.maxY = Math.max(vec3i.getY(), vec3i2.getY());
		this.maxZ = Math.max(vec3i.getZ(), vec3i2.getZ());
	}

	public BlockBox(int i, int j, int k, int l) {
		this.minX = i;
		this.minZ = j;
		this.maxX = k;
		this.maxZ = l;
		this.minY = 1;
		this.maxY = 512;
	}

	public boolean intersects(BlockBox blockBox) {
		return this.maxX >= blockBox.minX
			&& this.minX <= blockBox.maxX
			&& this.maxZ >= blockBox.minZ
			&& this.minZ <= blockBox.maxZ
			&& this.maxY >= blockBox.minY
			&& this.minY <= blockBox.maxY;
	}

	public boolean intersectsXZ(int i, int j, int k, int l) {
		return this.maxX >= i && this.minX <= k && this.maxZ >= j && this.minZ <= l;
	}

	public void encompass(BlockBox blockBox) {
		this.minX = Math.min(this.minX, blockBox.minX);
		this.minY = Math.min(this.minY, blockBox.minY);
		this.minZ = Math.min(this.minZ, blockBox.minZ);
		this.maxX = Math.max(this.maxX, blockBox.maxX);
		this.maxY = Math.max(this.maxY, blockBox.maxY);
		this.maxZ = Math.max(this.maxZ, blockBox.maxZ);
	}

	public void offset(int i, int j, int k) {
		this.minX += i;
		this.minY += j;
		this.minZ += k;
		this.maxX += i;
		this.maxY += j;
		this.maxZ += k;
	}

	public BlockBox translated(int i, int j, int k) {
		return new BlockBox(this.minX + i, this.minY + j, this.minZ + k, this.maxX + i, this.maxY + j, this.maxZ + k);
	}

	public boolean contains(Vec3i vec3i) {
		return vec3i.getX() >= this.minX
			&& vec3i.getX() <= this.maxX
			&& vec3i.getZ() >= this.minZ
			&& vec3i.getZ() <= this.maxZ
			&& vec3i.getY() >= this.minY
			&& vec3i.getY() <= this.maxY;
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

	@Environment(EnvType.CLIENT)
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
