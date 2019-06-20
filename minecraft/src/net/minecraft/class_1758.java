package net.minecraft;

public class class_1758 extends class_1792 {
	public class_1758(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		if (arg.field_9236) {
			return new class_1271<>(class_1269.field_5811, lv);
		} else {
			if (arg2.method_5765() && arg2.method_5854() instanceof class_1452) {
				class_1452 lv2 = (class_1452)arg2.method_5854();
				if (lv.method_7936() - lv.method_7919() >= 7 && lv2.method_6577()) {
					lv.method_7956(7, arg2, arg2x -> arg2x.method_20236(arg3));
					if (lv.method_7960()) {
						class_1799 lv3 = new class_1799(class_1802.field_8378);
						lv3.method_7980(lv.method_7969());
						return new class_1271<>(class_1269.field_5812, lv3);
					}

					return new class_1271<>(class_1269.field_5812, lv);
				}
			}

			arg2.method_7259(class_3468.field_15372.method_14956(this));
			return new class_1271<>(class_1269.field_5811, lv);
		}
	}
}
