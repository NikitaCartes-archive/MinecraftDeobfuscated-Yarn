package net.minecraft.util;

public class CuboidBlockIterator {
	private final int startX;
	private final int startY;
	private final int startZ;
	private final int endX;
	private final int endY;
	private final int endZ;
	private int x;
	private int y;
	private int z;
	private boolean complete;

	public CuboidBlockIterator(int startX, int startY, int startZ, int endX, int endY, int endZ) {
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
	}

	public boolean step() {
		if (!this.complete) {
			this.x = this.startX;
			this.y = this.startY;
			this.z = this.startZ;
			this.complete = true;
			return true;
		} else if (this.x == this.endX && this.y == this.endY && this.z == this.endZ) {
			return false;
		} else {
			if (this.x < this.endX) {
				this.x++;
			} else if (this.y < this.endY) {
				this.x = this.startX;
				this.y++;
			} else if (this.z < this.endZ) {
				this.x = this.startX;
				this.y = this.startY;
				this.z++;
			}

			return true;
		}
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public int method_20789() {
		int i = 0;
		if (this.x == this.startX || this.x == this.endX) {
			i++;
		}

		if (this.y == this.startY || this.y == this.endY) {
			i++;
		}

		if (this.z == this.startZ || this.z == this.endZ) {
			i++;
		}

		return i;
	}
}
