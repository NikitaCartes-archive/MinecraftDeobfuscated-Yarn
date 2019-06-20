package net.minecraft;

import java.util.EnumSet;

public class class_1371 extends class_1352 {
	private final class_1922 field_6538;
	private final class_1308 field_6541;
	private class_1309 field_6539;
	private int field_6540;

	public class_1371(class_1308 arg) {
		this.field_6541 = arg;
		this.field_6538 = arg.field_6002;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405, class_1352.class_4134.field_18406));
	}

	@Override
	public boolean method_6264() {
		class_1309 lv = this.field_6541.method_5968();
		if (lv == null) {
			return false;
		} else {
			this.field_6539 = lv;
			return true;
		}
	}

	@Override
	public boolean method_6266() {
		if (!this.field_6539.method_5805()) {
			return false;
		} else {
			return this.field_6541.method_5858(this.field_6539) > 225.0 ? false : !this.field_6541.method_5942().method_6357() || this.method_6264();
		}
	}

	@Override
	public void method_6270() {
		this.field_6539 = null;
		this.field_6541.method_5942().method_6340();
	}

	@Override
	public void method_6268() {
		this.field_6541.method_5988().method_6226(this.field_6539, 30.0F, 30.0F);
		double d = (double)(this.field_6541.method_17681() * 2.0F * this.field_6541.method_17681() * 2.0F);
		double e = this.field_6541.method_5649(this.field_6539.field_5987, this.field_6539.method_5829().field_1322, this.field_6539.field_6035);
		double f = 0.8;
		if (e > d && e < 16.0) {
			f = 1.33;
		} else if (e < 225.0) {
			f = 0.6;
		}

		this.field_6541.method_5942().method_6335(this.field_6539, f);
		this.field_6540 = Math.max(this.field_6540 - 1, 0);
		if (!(e > d)) {
			if (this.field_6540 <= 0) {
				this.field_6540 = 20;
				this.field_6541.method_6121(this.field_6539);
			}
		}
	}
}
