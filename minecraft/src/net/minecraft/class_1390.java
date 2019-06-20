package net.minecraft;

import java.util.EnumSet;

public class class_1390 extends class_1352 {
	private final class_3988 field_6610;

	public class_1390(class_3988 arg) {
		this.field_6610 = arg;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18407, class_1352.class_4134.field_18405));
	}

	@Override
	public boolean method_6264() {
		if (!this.field_6610.method_5805()) {
			return false;
		} else if (this.field_6610.method_5799()) {
			return false;
		} else if (!this.field_6610.field_5952) {
			return false;
		} else if (this.field_6610.field_6037) {
			return false;
		} else {
			class_1657 lv = this.field_6610.method_8257();
			if (lv == null) {
				return false;
			} else {
				return this.field_6610.method_5858(lv) > 16.0 ? false : lv.field_7512 != null;
			}
		}
	}

	@Override
	public void method_6269() {
		this.field_6610.method_5942().method_6340();
	}

	@Override
	public void method_6270() {
		this.field_6610.method_8259(null);
	}
}
