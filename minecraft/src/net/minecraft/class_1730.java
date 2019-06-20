package net.minecraft;

public class class_1730 extends class_1277 {
	private class_2611 field_7864;

	public class_1730() {
		super(27);
	}

	public void method_7661(class_2611 arg) {
		this.field_7864 = arg;
	}

	public void method_7659(class_2499 arg) {
		for (int i = 0; i < this.method_5439(); i++) {
			this.method_5447(i, class_1799.field_8037);
		}

		for (int i = 0; i < arg.size(); i++) {
			class_2487 lv = arg.method_10602(i);
			int j = lv.method_10571("Slot") & 255;
			if (j >= 0 && j < this.method_5439()) {
				this.method_5447(j, class_1799.method_7915(lv));
			}
		}
	}

	public class_2499 method_7660() {
		class_2499 lv = new class_2499();

		for (int i = 0; i < this.method_5439(); i++) {
			class_1799 lv2 = this.method_5438(i);
			if (!lv2.method_7960()) {
				class_2487 lv3 = new class_2487();
				lv3.method_10567("Slot", (byte)i);
				lv2.method_7953(lv3);
				lv.add(lv3);
			}
		}

		return lv;
	}

	@Override
	public boolean method_5443(class_1657 arg) {
		return this.field_7864 != null && !this.field_7864.method_11218(arg) ? false : super.method_5443(arg);
	}

	@Override
	public void method_5435(class_1657 arg) {
		if (this.field_7864 != null) {
			this.field_7864.method_11219();
		}

		super.method_5435(arg);
	}

	@Override
	public void method_5432(class_1657 arg) {
		if (this.field_7864 != null) {
			this.field_7864.method_11220();
		}

		super.method_5432(arg);
		this.field_7864 = null;
	}
}
