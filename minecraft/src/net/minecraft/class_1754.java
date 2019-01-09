package net.minecraft;

import java.util.List;

public class class_1754 extends class_1792 {
	public class_1754(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		List<class_1295> list = arg.method_8390(
			class_1295.class, arg2.method_5829().method_1014(2.0), argx -> argx != null && argx.method_5805() && argx.method_5601() instanceof class_1510
		);
		class_1799 lv = arg2.method_5998(arg3);
		if (!list.isEmpty()) {
			class_1295 lv2 = (class_1295)list.get(0);
			lv2.method_5603(lv2.method_5599() - 0.5F);
			arg.method_8465(null, arg2.field_5987, arg2.field_6010, arg2.field_6035, class_3417.field_15029, class_3419.field_15254, 1.0F, 1.0F);
			return new class_1271<>(class_1269.field_5812, this.method_7725(lv, arg2, new class_1799(class_1802.field_8613)));
		} else {
			class_239 lv3 = this.method_7872(arg, arg2, true);
			if (lv3 == null) {
				return new class_1271<>(class_1269.field_5811, lv);
			} else {
				if (lv3.field_1330 == class_239.class_240.field_1332) {
					class_2338 lv4 = lv3.method_1015();
					if (!arg.method_8505(arg2, lv4)) {
						return new class_1271<>(class_1269.field_5811, lv);
					}

					if (arg.method_8316(lv4).method_15767(class_3486.field_15517)) {
						arg.method_8465(arg2, arg2.field_5987, arg2.field_6010, arg2.field_6035, class_3417.field_14779, class_3419.field_15254, 1.0F, 1.0F);
						return new class_1271<>(
							class_1269.field_5812, this.method_7725(lv, arg2, class_1844.method_8061(new class_1799(class_1802.field_8574), class_1847.field_8991))
						);
					}
				}

				return new class_1271<>(class_1269.field_5811, lv);
			}
		}
	}

	protected class_1799 method_7725(class_1799 arg, class_1657 arg2, class_1799 arg3) {
		arg.method_7934(1);
		arg2.method_7259(class_3468.field_15372.method_14956(this));
		if (arg.method_7960()) {
			return arg3;
		} else {
			if (!arg2.field_7514.method_7394(arg3)) {
				arg2.method_7328(arg3, false);
			}

			return arg;
		}
	}
}
