package net.minecraft;

import java.util.EnumSet;

public class class_1359 extends class_1352 {
	private final class_1308 field_6476;
	private class_1309 field_6477;
	private final float field_6475;

	public class_1359(class_1308 arg, float f) {
		this.field_6476 = arg;
		this.field_6475 = f;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18407, class_1352.class_4134.field_18405));
	}

	@Override
	public boolean method_6264() {
		if (this.field_6476.method_5782()) {
			return false;
		} else {
			this.field_6477 = this.field_6476.method_5968();
			if (this.field_6477 == null) {
				return false;
			} else {
				double d = this.field_6476.method_5858(this.field_6477);
				if (d < 4.0 || d > 16.0) {
					return false;
				} else {
					return !this.field_6476.field_5952 ? false : this.field_6476.method_6051().nextInt(5) == 0;
				}
			}
		}
	}

	@Override
	public boolean method_6266() {
		return !this.field_6476.field_5952;
	}

	@Override
	public void method_6269() {
		class_243 lv = this.field_6476.method_18798();
		class_243 lv2 = new class_243(this.field_6477.field_5987 - this.field_6476.field_5987, 0.0, this.field_6477.field_6035 - this.field_6476.field_6035);
		if (lv2.method_1027() > 1.0E-7) {
			lv2 = lv2.method_1029().method_1021(0.4).method_1019(lv.method_1021(0.2));
		}

		this.field_6476.method_18800(lv2.field_1352, (double)this.field_6475, lv2.field_1351);
	}
}
