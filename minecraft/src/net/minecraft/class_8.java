package net.minecraft;

public abstract class class_8 {
	protected class_1922 field_26;
	protected class_1308 field_33;
	protected final class_3525<class_9> field_32 = new class_3525<>();
	protected int field_31;
	protected int field_30;
	protected int field_28;
	protected boolean field_29;
	protected boolean field_27;
	protected boolean field_25;

	public void method_12(class_1922 arg, class_1308 arg2) {
		this.field_26 = arg;
		this.field_33 = arg2;
		this.field_32.method_15321();
		this.field_31 = class_3532.method_15375(arg2.field_5998 + 1.0F);
		this.field_30 = class_3532.method_15375(arg2.field_6019 + 1.0F);
		this.field_28 = class_3532.method_15375(arg2.field_5998 + 1.0F);
	}

	public void method_19() {
		this.field_26 = null;
		this.field_33 = null;
	}

	protected class_9 method_13(int i, int j, int k) {
		int l = class_9.method_30(i, j, k);
		class_9 lv = this.field_32.method_15316(l);
		if (lv == null) {
			lv = new class_9(i, j, k);
			this.field_32.method_15313(l, lv);
		}

		return lv;
	}

	public abstract class_9 method_21();

	public abstract class_9 method_16(double d, double e, double f);

	public abstract int method_18(class_9[] args, class_9 arg, class_9 arg2, float f);

	public abstract class_7 method_17(class_1922 arg, int i, int j, int k, class_1308 arg2, int l, int m, int n, boolean bl, boolean bl2);

	public abstract class_7 method_25(class_1922 arg, int i, int j, int k);

	public void method_15(boolean bl) {
		this.field_29 = bl;
	}

	public void method_20(boolean bl) {
		this.field_27 = bl;
	}

	public void method_14(boolean bl) {
		this.field_25 = bl;
	}

	public boolean method_23() {
		return this.field_29;
	}

	public boolean method_24() {
		return this.field_27;
	}

	public boolean method_22() {
		return this.field_25;
	}
}
