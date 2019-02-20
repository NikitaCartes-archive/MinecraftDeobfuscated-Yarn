package net.minecraft.sortme;

public class CuboidBlockIterator {
	private final int startX;
	private final int startY;
	private final int startZ;
	private final int endX;
	private final int endY;
	private final int endZ;
	private int field_18233;
	private int field_18234;
	private int field_18235;
	private boolean field_18236;

	public CuboidBlockIterator(int i, int j, int k, int l, int m, int n) {
		this.startX = i;
		this.startY = j;
		this.startZ = k;
		this.endX = l;
		this.endY = m;
		this.endZ = n;
	}

	public boolean step() {
		if (!this.field_18236) {
			this.field_18233 = this.startX;
			this.field_18234 = this.startY;
			this.field_18235 = this.startZ;
			this.field_18236 = true;
			return true;
		} else if (this.field_18233 == this.endX && this.field_18234 == this.endY && this.field_18235 == this.endZ) {
			return false;
		} else {
			if (this.field_18233 < this.endX) {
				this.field_18233++;
			} else if (this.field_18234 < this.endY) {
				this.field_18233 = this.startX;
				this.field_18234++;
			} else if (this.field_18235 < this.endZ) {
				this.field_18233 = this.startX;
				this.field_18234 = this.startY;
				this.field_18235++;
			}

			return true;
		}
	}

	public int method_18671() {
		return this.field_18233;
	}

	public int method_18672() {
		return this.field_18234;
	}

	public int method_18673() {
		return this.field_18235;
	}
}
