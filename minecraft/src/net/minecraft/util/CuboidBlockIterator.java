package net.minecraft.util;

public class CuboidBlockIterator {
	private int startX;
	private int startY;
	private int startZ;
	private int endX;
	private int endY;
	private int endZ;
	private int field_23112;
	private int field_23113;
	private int x;
	private int y;
	private int z;

	public CuboidBlockIterator(int startX, int startY, int startZ, int endX, int endY, int endZ) {
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.endX = endX - startX + 1;
		this.endY = endY - startY + 1;
		this.endZ = endZ - startZ + 1;
		this.field_23112 = this.endX * this.endY * this.endZ;
	}

	public boolean step() {
		if (this.field_23113 == this.field_23112) {
			return false;
		} else {
			this.x = this.field_23113 % this.endX;
			int i = this.field_23113 / this.endX;
			this.y = i % this.endY;
			this.z = i / this.endY;
			this.field_23113++;
			return true;
		}
	}

	public int getX() {
		return this.startX + this.x;
	}

	public int getY() {
		return this.startY + this.y;
	}

	public int getZ() {
		return this.startZ + this.z;
	}

	public int getEdgeCoordinatesCount() {
		int i = 0;
		if (this.x == 0 || this.x == this.endX - 1) {
			i++;
		}

		if (this.y == 0 || this.y == this.endY - 1) {
			i++;
		}

		if (this.z == 0 || this.z == this.endZ - 1) {
			i++;
		}

		return i;
	}
}
