package net.minecraft;

public class class_5 {
	private class_9[] field_1 = new class_9[128];
	private int field_2;

	public class_9 method_2(class_9 arg) {
		if (arg.field_37 >= 0) {
			throw new IllegalStateException("OW KNOWS!");
		} else {
			if (this.field_2 == this.field_1.length) {
				class_9[] lvs = new class_9[this.field_2 << 1];
				System.arraycopy(this.field_1, 0, lvs, 0, this.field_2);
				this.field_1 = lvs;
			}

			this.field_1[this.field_2] = arg;
			arg.field_37 = this.field_2;
			this.method_4(this.field_2++);
			return arg;
		}
	}

	public void method_5() {
		this.field_2 = 0;
	}

	public class_9 method_6() {
		class_9 lv = this.field_1[0];
		this.field_1[0] = this.field_1[--this.field_2];
		this.field_1[this.field_2] = null;
		if (this.field_2 > 0) {
			this.method_7(0);
		}

		lv.field_37 = -1;
		return lv;
	}

	public void method_3(class_9 arg, float f) {
		float g = arg.field_47;
		arg.field_47 = f;
		if (f < g) {
			this.method_4(arg.field_37);
		} else {
			this.method_7(arg.field_37);
		}
	}

	private void method_4(int i) {
		class_9 lv = this.field_1[i];
		float f = lv.field_47;

		while (i > 0) {
			int j = i - 1 >> 1;
			class_9 lv2 = this.field_1[j];
			if (!(f < lv2.field_47)) {
				break;
			}

			this.field_1[i] = lv2;
			lv2.field_37 = i;
			i = j;
		}

		this.field_1[i] = lv;
		lv.field_37 = i;
	}

	private void method_7(int i) {
		class_9 lv = this.field_1[i];
		float f = lv.field_47;

		while (true) {
			int j = 1 + (i << 1);
			int k = j + 1;
			if (j >= this.field_2) {
				break;
			}

			class_9 lv2 = this.field_1[j];
			float g = lv2.field_47;
			class_9 lv3;
			float h;
			if (k >= this.field_2) {
				lv3 = null;
				h = Float.POSITIVE_INFINITY;
			} else {
				lv3 = this.field_1[k];
				h = lv3.field_47;
			}

			if (g < h) {
				if (!(g < f)) {
					break;
				}

				this.field_1[i] = lv2;
				lv2.field_37 = i;
				i = j;
			} else {
				if (!(h < f)) {
					break;
				}

				this.field_1[i] = lv3;
				lv3.field_37 = i;
				i = k;
			}
		}

		this.field_1[i] = lv;
		lv.field_37 = i;
	}

	public boolean method_8() {
		return this.field_2 == 0;
	}
}
