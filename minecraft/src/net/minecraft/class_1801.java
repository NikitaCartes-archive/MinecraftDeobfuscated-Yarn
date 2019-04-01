package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1801 extends class_1792 {
	private static final Logger field_8042 = LogManager.getLogger();

	public class_1801(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		class_2487 lv2 = lv.method_7969();
		if (!arg2.field_7503.field_7477) {
			arg2.method_6122(arg3, class_1799.field_8037);
		}

		if (lv2 != null && lv2.method_10573("Recipes", 9)) {
			if (!arg.field_9236) {
				class_2499 lv3 = lv2.method_10554("Recipes", 8);
				List<class_1860<?>> list = Lists.<class_1860<?>>newArrayList();
				class_1863 lv4 = arg.method_8503().method_3772();

				for (int i = 0; i < lv3.size(); i++) {
					String string = lv3.method_10608(i);
					Optional<? extends class_1860<?>> optional = lv4.method_8130(new class_2960(string));
					if (!optional.isPresent()) {
						field_8042.error("Invalid recipe: {}", string);
						return new class_1271<>(class_1269.field_5814, lv);
					}

					list.add(optional.get());
				}

				arg2.method_7254(list);
				arg2.method_7259(class_3468.field_15372.method_14956(this));
			}

			return new class_1271<>(class_1269.field_5812, lv);
		} else {
			field_8042.error("Tag not valid: {}", lv2);
			return new class_1271<>(class_1269.field_5814, lv);
		}
	}
}
