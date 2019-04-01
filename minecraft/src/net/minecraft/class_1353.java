package net.minecraft;

import java.util.List;

public class class_1353 extends class_1352 {
	private final class_1429 field_6455;
	private class_1429 field_6452;
	private final double field_6453;
	private int field_6454;

	public class_1353(class_1429 arg, double d) {
		this.field_6455 = arg;
		this.field_6453 = d;
	}

	@Override
	public boolean method_6264() {
		if (this.field_6455.method_5618() >= 0) {
			return false;
		} else {
			List<class_1429> list = this.field_6455.field_6002.method_18467(this.field_6455.getClass(), this.field_6455.method_5829().method_1009(8.0, 4.0, 8.0));
			class_1429 lv = null;
			double d = Double.MAX_VALUE;

			for (class_1429 lv2 : list) {
				if (lv2.method_5618() >= 0) {
					double e = this.field_6455.method_5858(lv2);
					if (!(e > d)) {
						d = e;
						lv = lv2;
					}
				}
			}

			if (lv == null) {
				return false;
			} else if (d < 9.0) {
				return false;
			} else {
				this.field_6452 = lv;
				return true;
			}
		}
	}

	@Override
	public boolean method_6266() {
		if (this.field_6455.method_5618() >= 0) {
			return false;
		} else if (!this.field_6452.method_5805()) {
			return false;
		} else {
			double d = this.field_6455.method_5858(this.field_6452);
			return !(d < 9.0) && !(d > 256.0);
		}
	}

	@Override
	public void method_6269() {
		this.field_6454 = 0;
	}

	@Override
	public void method_6270() {
		this.field_6452 = null;
	}

	@Override
	public void method_6268() {
		if (--this.field_6454 <= 0) {
			this.field_6454 = 10;
			this.field_6455.method_5942().method_6335(this.field_6452, this.field_6453);
		}
	}
}
