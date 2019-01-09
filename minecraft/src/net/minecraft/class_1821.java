package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;

public class class_1821 extends class_1766 {
	private static final Set<class_2248> field_8913 = Sets.<class_2248>newHashSet(
		class_2246.field_10460,
		class_2246.field_10566,
		class_2246.field_10253,
		class_2246.field_10520,
		class_2246.field_10362,
		class_2246.field_10219,
		class_2246.field_10255,
		class_2246.field_10402,
		class_2246.field_10102,
		class_2246.field_10534,
		class_2246.field_10491,
		class_2246.field_10477,
		class_2246.field_10114,
		class_2246.field_10194,
		class_2246.field_10197,
		class_2246.field_10022,
		class_2246.field_10300,
		class_2246.field_10321,
		class_2246.field_10145,
		class_2246.field_10133,
		class_2246.field_10522,
		class_2246.field_10353,
		class_2246.field_10628,
		class_2246.field_10233,
		class_2246.field_10404,
		class_2246.field_10456,
		class_2246.field_10023,
		class_2246.field_10529,
		class_2246.field_10287,
		class_2246.field_10506
	);
	protected static final Map<class_2248, class_2680> field_8912 = Maps.<class_2248, class_2680>newHashMap(
		ImmutableMap.of(class_2246.field_10219, class_2246.field_10194.method_9564())
	);

	public class_1821(class_1832 arg, float f, float g, class_1792.class_1793 arg2) {
		super(f, g, arg, field_8913, arg2);
	}

	@Override
	public boolean method_7856(class_2680 arg) {
		class_2248 lv = arg.method_11614();
		return lv == class_2246.field_10477 || lv == class_2246.field_10491;
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		if (arg.method_8038() != class_2350.field_11033 && lv.method_8320(lv2.method_10084()).method_11588()) {
			class_2680 lv3 = (class_2680)field_8912.get(lv.method_8320(lv2).method_11614());
			if (lv3 != null) {
				class_1657 lv4 = arg.method_8036();
				lv.method_8396(lv4, lv2, class_3417.field_14616, class_3419.field_15245, 1.0F, 1.0F);
				if (!lv.field_9236) {
					lv.method_8652(lv2, lv3, 11);
					if (lv4 != null) {
						arg.method_8041().method_7956(1, lv4);
					}
				}

				return class_1269.field_5812;
			}
		}

		return class_1269.field_5811;
	}
}
