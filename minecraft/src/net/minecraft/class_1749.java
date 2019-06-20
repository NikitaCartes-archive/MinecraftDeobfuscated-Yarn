package net.minecraft;

import java.util.List;
import java.util.function.Predicate;

public class class_1749 extends class_1792 {
	private static final Predicate<class_1297> field_17497 = class_1301.field_6155.and(class_1297::method_5863);
	private final class_1690.class_1692 field_7902;

	public class_1749(class_1690.class_1692 arg, class_1792.class_1793 arg2) {
		super(arg2);
		this.field_7902 = arg;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		class_239 lv2 = method_7872(arg, arg2, class_3959.class_242.field_1347);
		if (lv2.method_17783() == class_239.class_240.field_1333) {
			return new class_1271<>(class_1269.field_5811, lv);
		} else {
			class_243 lv3 = arg2.method_5828(1.0F);
			double d = 5.0;
			List<class_1297> list = arg.method_8333(arg2, arg2.method_5829().method_18804(lv3.method_1021(5.0)).method_1014(1.0), field_17497);
			if (!list.isEmpty()) {
				class_243 lv4 = arg2.method_5836(1.0F);

				for (class_1297 lv5 : list) {
					class_238 lv6 = lv5.method_5829().method_1014((double)lv5.method_5871());
					if (lv6.method_1006(lv4)) {
						return new class_1271<>(class_1269.field_5811, lv);
					}
				}
			}

			if (lv2.method_17783() == class_239.class_240.field_1332) {
				class_1690 lv7 = new class_1690(arg, lv2.method_17784().field_1352, lv2.method_17784().field_1351, lv2.method_17784().field_1350);
				lv7.method_7541(this.field_7902);
				lv7.field_6031 = arg2.field_6031;
				if (!arg.method_8587(lv7, lv7.method_5829().method_1014(-0.1))) {
					return new class_1271<>(class_1269.field_5814, lv);
				} else {
					if (!arg.field_9236) {
						arg.method_8649(lv7);
					}

					if (!arg2.field_7503.field_7477) {
						lv.method_7934(1);
					}

					arg2.method_7259(class_3468.field_15372.method_14956(this));
					return new class_1271<>(class_1269.field_5812, lv);
				}
			} else {
				return new class_1271<>(class_1269.field_5811, lv);
			}
		}
	}
}
