package net.minecraft;

public class class_1370 extends class_1352 {
	private final class_1314 field_6536;
	private double field_6535;
	private double field_6534;
	private double field_6533;
	private final double field_6537;

	public class_1370(class_1314 arg, double d) {
		this.field_6536 = arg;
		this.field_6537 = d;
		this.method_6265(1);
	}

	@Override
	public boolean method_6264() {
		if (this.field_6536.method_6152()) {
			return false;
		} else {
			class_2338 lv = this.field_6536.method_6141();
			class_243 lv2 = class_1414.method_6373(
				this.field_6536, 16, 7, new class_243((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260())
			);
			if (lv2 == null) {
				return false;
			} else {
				this.field_6535 = lv2.field_1352;
				this.field_6534 = lv2.field_1351;
				this.field_6533 = lv2.field_1350;
				return true;
			}
		}
	}

	@Override
	public boolean method_6266() {
		return !this.field_6536.method_5942().method_6357();
	}

	@Override
	public void method_6269() {
		this.field_6536.method_5942().method_6337(this.field_6535, this.field_6534, this.field_6533, this.field_6537);
	}
}
