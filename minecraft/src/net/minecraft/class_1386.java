package net.minecraft;

import java.util.EnumSet;

public class class_1386 extends class_1352 {
	private final class_1321 field_6597;
	private boolean field_6598;

	public class_1386(class_1321 arg) {
		this.field_6597 = arg;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18407, class_1352.class_4134.field_18405));
	}

	@Override
	public boolean method_6264() {
		if (!this.field_6597.method_6181()) {
			return false;
		} else if (this.field_6597.method_5816()) {
			return false;
		} else if (!this.field_6597.field_5952) {
			return false;
		} else {
			class_1309 lv = this.field_6597.method_6177();
			if (lv == null) {
				return true;
			} else {
				return this.field_6597.method_5858(lv) < 144.0 && lv.method_6065() != null ? false : this.field_6598;
			}
		}
	}

	@Override
	public void method_6269() {
		this.field_6597.method_5942().method_6340();
		this.field_6597.method_6179(true);
	}

	@Override
	public void method_6270() {
		this.field_6597.method_6179(false);
	}

	public void method_6311(boolean bl) {
		this.field_6598 = bl;
	}
}
