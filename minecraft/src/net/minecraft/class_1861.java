package net.minecraft;

public class class_1861 extends class_1869 {
	public class_1861(class_2960 arg) {
		super(
			arg,
			"",
			3,
			3,
			class_2371.method_10212(
				class_1856.field_9017,
				class_1856.method_8091(class_1802.field_8407),
				class_1856.method_8091(class_1802.field_8407),
				class_1856.method_8091(class_1802.field_8407),
				class_1856.method_8091(class_1802.field_8407),
				class_1856.method_8091(class_1802.field_8204),
				class_1856.method_8091(class_1802.field_8407),
				class_1856.method_8091(class_1802.field_8407),
				class_1856.method_8091(class_1802.field_8407),
				class_1856.method_8091(class_1802.field_8407)
			),
			new class_1799(class_1802.field_8895)
		);
	}

	@Override
	public boolean method_17728(class_1715 arg, class_1937 arg2) {
		if (!super.method_17728(arg, arg2)) {
			return false;
		} else {
			class_1799 lv = class_1799.field_8037;

			for (int i = 0; i < arg.method_5439() && lv.method_7960(); i++) {
				class_1799 lv2 = arg.method_5438(i);
				if (lv2.method_7909() == class_1802.field_8204) {
					lv = lv2;
				}
			}

			if (lv.method_7960()) {
				return false;
			} else {
				class_22 lv3 = class_1806.method_8001(lv, arg2);
				if (lv3 == null) {
					return false;
				} else {
					return this.method_8120(lv3) ? false : lv3.field_119 < 4;
				}
			}
		}
	}

	private boolean method_8120(class_22 arg) {
		if (arg.field_117 != null) {
			for (class_20 lv : arg.field_117.values()) {
				if (lv.method_93() == class_20.class_21.field_88 || lv.method_93() == class_20.class_21.field_98) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public class_1799 method_17727(class_1715 arg) {
		class_1799 lv = class_1799.field_8037;

		for (int i = 0; i < arg.method_5439() && lv.method_7960(); i++) {
			class_1799 lv2 = arg.method_5438(i);
			if (lv2.method_7909() == class_1802.field_8204) {
				lv = lv2;
			}
		}

		lv = lv.method_7972();
		lv.method_7939(1);
		lv.method_7948().method_10569("map_scale_direction", 1);
		return lv;
	}

	@Override
	public boolean method_8118() {
		return true;
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_9039;
	}
}
