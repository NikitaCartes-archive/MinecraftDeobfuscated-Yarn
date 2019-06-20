package net.minecraft;

public class class_3980 {
	private final int field_17683;
	private final int field_17684;
	private final int field_17685;
	private final int field_17686;
	private final int field_17687;
	private final int field_17688;
	private int field_18233;
	private int field_18234;
	private int field_18235;
	private boolean field_18236;

	public class_3980(int i, int j, int k, int l, int m, int n) {
		this.field_17683 = i;
		this.field_17684 = j;
		this.field_17685 = k;
		this.field_17686 = l;
		this.field_17687 = m;
		this.field_17688 = n;
	}

	public boolean method_17963() {
		if (!this.field_18236) {
			this.field_18233 = this.field_17683;
			this.field_18234 = this.field_17684;
			this.field_18235 = this.field_17685;
			this.field_18236 = true;
			return true;
		} else if (this.field_18233 == this.field_17686 && this.field_18234 == this.field_17687 && this.field_18235 == this.field_17688) {
			return false;
		} else {
			if (this.field_18233 < this.field_17686) {
				this.field_18233++;
			} else if (this.field_18234 < this.field_17687) {
				this.field_18233 = this.field_17683;
				this.field_18234++;
			} else if (this.field_18235 < this.field_17688) {
				this.field_18233 = this.field_17683;
				this.field_18234 = this.field_17684;
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

	public int method_20789() {
		int i = 0;
		if (this.field_18233 == this.field_17683 || this.field_18233 == this.field_17686) {
			i++;
		}

		if (this.field_18234 == this.field_17684 || this.field_18234 == this.field_17687) {
			i++;
		}

		if (this.field_18235 == this.field_17685 || this.field_18235 == this.field_17688) {
			i++;
		}

		return i;
	}
}
