package net.minecraft;

import java.util.Random;

public abstract class class_3418 extends class_3443 {
	protected final int field_15244;
	protected final int field_15243;
	protected final int field_15242;
	protected int field_15241 = -1;

	protected class_3418(class_3773 arg, Random random, int i, int j, int k, int l, int m, int n) {
		super(arg, 0);
		this.field_15244 = l;
		this.field_15243 = m;
		this.field_15242 = n;
		this.method_14926(class_2350.class_2353.field_11062.method_10183(random));
		if (this.method_14934().method_10166() == class_2350.class_2351.field_11051) {
			this.field_15315 = new class_3341(i, j, k, i + l - 1, j + m - 1, k + n - 1);
		} else {
			this.field_15315 = new class_3341(i, j, k, i + n - 1, j + m - 1, k + l - 1);
		}
	}

	protected class_3418(class_3773 arg, class_2487 arg2) {
		super(arg, arg2);
		this.field_15244 = arg2.method_10550("Width");
		this.field_15243 = arg2.method_10550("Height");
		this.field_15242 = arg2.method_10550("Depth");
		this.field_15241 = arg2.method_10550("HPos");
	}

	@Override
	protected void method_14943(class_2487 arg) {
		arg.method_10569("Width", this.field_15244);
		arg.method_10569("Height", this.field_15243);
		arg.method_10569("Depth", this.field_15242);
		arg.method_10569("HPos", this.field_15241);
	}

	protected boolean method_14839(class_1936 arg, class_3341 arg2, int i) {
		if (this.field_15241 >= 0) {
			return true;
		} else {
			int j = 0;
			int k = 0;
			class_2338.class_2339 lv = new class_2338.class_2339();

			for (int l = this.field_15315.field_14379; l <= this.field_15315.field_14376; l++) {
				for (int m = this.field_15315.field_14381; m <= this.field_15315.field_14378; m++) {
					lv.method_10103(m, 64, l);
					if (arg2.method_14662(lv)) {
						j += arg.method_8598(class_2902.class_2903.field_13203, lv).method_10264();
						k++;
					}
				}
			}

			if (k == 0) {
				return false;
			} else {
				this.field_15241 = j / k;
				this.field_15315.method_14661(0, this.field_15241 - this.field_15315.field_14380 + i, 0);
				return true;
			}
		}
	}
}
