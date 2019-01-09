package net.minecraft;

public class class_1351 extends class_1350 {
	public class_1351(class_1321 arg, double d, float f, float g) {
		super(arg, d, f, g);
	}

	@Override
	protected boolean method_6263(int i, int j, int k, int l, int m) {
		class_2338 lv = new class_2338(i + l, k - 1, j + m);
		class_2680 lv2 = this.field_6445.method_8320(lv);
		return (lv2.method_11631(this.field_6445, lv) || lv2.method_11602(class_3481.field_15503))
			&& this.field_6445.method_8623(new class_2338(i + l, k, j + m))
			&& this.field_6445.method_8623(new class_2338(i + l, k + 1, j + m));
	}
}
