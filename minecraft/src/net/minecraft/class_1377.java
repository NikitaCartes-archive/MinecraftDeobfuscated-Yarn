package net.minecraft;

import java.util.List;

public class class_1377 extends class_1352 {
	private final class_1646 field_6560;
	private class_1309 field_6559;
	private final double field_6557;
	private int field_6558;

	public class_1377(class_1646 arg, double d) {
		this.field_6560 = arg;
		this.field_6557 = d;
		this.method_6265(1);
	}

	@Override
	public boolean method_6264() {
		if (this.field_6560.method_5618() >= 0) {
			return false;
		} else if (this.field_6560.method_6051().nextInt(400) != 0) {
			return false;
		} else {
			List<class_1646> list = this.field_6560.field_6002.method_8403(class_1646.class, this.field_6560.method_5829().method_1009(6.0, 3.0, 6.0));
			double d = Double.MAX_VALUE;

			for (class_1646 lv : list) {
				if (lv != this.field_6560 && !lv.method_7236() && lv.method_5618() < 0) {
					double e = lv.method_5858(this.field_6560);
					if (!(e > d)) {
						d = e;
						this.field_6559 = lv;
					}
				}
			}

			if (this.field_6559 == null) {
				class_243 lv2 = class_1414.method_6375(this.field_6560, 16, 3);
				if (lv2 == null) {
					return false;
				}
			}

			return true;
		}
	}

	@Override
	public boolean method_6266() {
		return this.field_6558 > 0;
	}

	@Override
	public void method_6269() {
		if (this.field_6559 != null) {
			this.field_6560.method_7220(true);
		}

		this.field_6558 = 1000;
	}

	@Override
	public void method_6270() {
		this.field_6560.method_7220(false);
		this.field_6559 = null;
	}

	@Override
	public void method_6268() {
		this.field_6558--;
		if (this.field_6559 != null) {
			if (this.field_6560.method_5858(this.field_6559) > 4.0) {
				this.field_6560.method_5942().method_6335(this.field_6559, this.field_6557);
			}
		} else if (this.field_6560.method_5942().method_6357()) {
			class_243 lv = class_1414.method_6375(this.field_6560, 16, 3);
			if (lv == null) {
				return;
			}

			this.field_6560.method_5942().method_6337(lv.field_1352, lv.field_1351, lv.field_1350, this.field_6557);
		}
	}
}
