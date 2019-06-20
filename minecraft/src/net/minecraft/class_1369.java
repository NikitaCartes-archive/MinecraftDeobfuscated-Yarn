package net.minecraft;

import java.util.EnumSet;

public class class_1369 extends class_1352 {
	private final class_1314 field_6528;
	private class_1309 field_6529;
	private double field_6527;
	private double field_6526;
	private double field_6531;
	private final double field_6530;
	private final float field_6532;

	public class_1369(class_1314 arg, double d, float f) {
		this.field_6528 = arg;
		this.field_6530 = d;
		this.field_6532 = f;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
	}

	@Override
	public boolean method_6264() {
		this.field_6529 = this.field_6528.method_5968();
		if (this.field_6529 == null) {
			return false;
		} else if (this.field_6529.method_5858(this.field_6528) > (double)(this.field_6532 * this.field_6532)) {
			return false;
		} else {
			class_243 lv = class_1414.method_6373(
				this.field_6528, 16, 7, new class_243(this.field_6529.field_5987, this.field_6529.field_6010, this.field_6529.field_6035)
			);
			if (lv == null) {
				return false;
			} else {
				this.field_6527 = lv.field_1352;
				this.field_6526 = lv.field_1351;
				this.field_6531 = lv.field_1350;
				return true;
			}
		}
	}

	@Override
	public boolean method_6266() {
		return !this.field_6528.method_5942().method_6357()
			&& this.field_6529.method_5805()
			&& this.field_6529.method_5858(this.field_6528) < (double)(this.field_6532 * this.field_6532);
	}

	@Override
	public void method_6270() {
		this.field_6529 = null;
	}

	@Override
	public void method_6269() {
		this.field_6528.method_5942().method_6337(this.field_6527, this.field_6526, this.field_6531, this.field_6530);
	}
}
