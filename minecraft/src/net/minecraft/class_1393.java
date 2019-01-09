package net.minecraft;

public class class_1393 extends class_1352 {
	private final class_1314 field_6625;

	public class_1393(class_1314 arg) {
		this.field_6625 = arg;
	}

	@Override
	public boolean method_6264() {
		return this.field_6625.field_5952 && !this.field_6625.field_6002.method_8316(new class_2338(this.field_6625)).method_15767(class_3486.field_15517);
	}

	@Override
	public void method_6269() {
		class_2338 lv = null;

		for (class_2338 lv2 : class_2338.class_2339.method_10068(
			class_3532.method_15357(this.field_6625.field_5987 - 2.0),
			class_3532.method_15357(this.field_6625.field_6010 - 2.0),
			class_3532.method_15357(this.field_6625.field_6035 - 2.0),
			class_3532.method_15357(this.field_6625.field_5987 + 2.0),
			class_3532.method_15357(this.field_6625.field_6010),
			class_3532.method_15357(this.field_6625.field_6035 + 2.0)
		)) {
			if (this.field_6625.field_6002.method_8316(lv2).method_15767(class_3486.field_15517)) {
				lv = lv2;
				break;
			}
		}

		if (lv != null) {
			this.field_6625.method_5962().method_6239((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260(), 1.0);
		}
	}
}
