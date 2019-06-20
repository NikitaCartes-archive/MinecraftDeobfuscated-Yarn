package net.minecraft;

import java.util.EnumSet;

public class class_1387 extends class_1352 {
	private final class_1496 field_6602;
	private final double field_6601;
	private double field_6600;
	private double field_6599;
	private double field_6603;

	public class_1387(class_1496 arg, double d) {
		this.field_6602 = arg;
		this.field_6601 = d;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
	}

	@Override
	public boolean method_6264() {
		if (!this.field_6602.method_6727() && this.field_6602.method_5782()) {
			class_243 lv = class_1414.method_6375(this.field_6602, 5, 4);
			if (lv == null) {
				return false;
			} else {
				this.field_6600 = lv.field_1352;
				this.field_6599 = lv.field_1351;
				this.field_6603 = lv.field_1350;
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void method_6269() {
		this.field_6602.method_5942().method_6337(this.field_6600, this.field_6599, this.field_6603, this.field_6601);
	}

	@Override
	public boolean method_6266() {
		return !this.field_6602.method_6727() && !this.field_6602.method_5942().method_6357() && this.field_6602.method_5782();
	}

	@Override
	public void method_6268() {
		if (!this.field_6602.method_6727() && this.field_6602.method_6051().nextInt(50) == 0) {
			class_1297 lv = (class_1297)this.field_6602.method_5685().get(0);
			if (lv == null) {
				return;
			}

			if (lv instanceof class_1657) {
				int i = this.field_6602.method_6729();
				int j = this.field_6602.method_6755();
				if (j > 0 && this.field_6602.method_6051().nextInt(j) < i) {
					this.field_6602.method_6752((class_1657)lv);
					return;
				}

				this.field_6602.method_6745(5);
			}

			this.field_6602.method_5772();
			this.field_6602.method_6757();
			this.field_6602.field_6002.method_8421(this.field_6602, (byte)6);
		}
	}
}
