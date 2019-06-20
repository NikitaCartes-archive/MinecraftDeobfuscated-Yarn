package net.minecraft;

import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Set;

public class class_1743 extends class_1766 {
	private static final Set<class_2248> field_7899 = Sets.<class_2248>newHashSet(
		class_2246.field_10161,
		class_2246.field_9975,
		class_2246.field_10148,
		class_2246.field_10334,
		class_2246.field_10218,
		class_2246.field_10075,
		class_2246.field_10504,
		class_2246.field_10126,
		class_2246.field_10155,
		class_2246.field_10307,
		class_2246.field_10303,
		class_2246.field_9999,
		class_2246.field_10178,
		class_2246.field_10431,
		class_2246.field_10037,
		class_2246.field_10511,
		class_2246.field_10306,
		class_2246.field_10533,
		class_2246.field_10010,
		class_2246.field_10034,
		class_2246.field_10261,
		class_2246.field_10147,
		class_2246.field_10009,
		class_2246.field_10545,
		class_2246.field_9983,
		class_2246.field_16492,
		class_2246.field_10057,
		class_2246.field_10066,
		class_2246.field_10417,
		class_2246.field_10553,
		class_2246.field_10493,
		class_2246.field_10278,
		class_2246.field_10484,
		class_2246.field_10332,
		class_2246.field_10592,
		class_2246.field_10026,
		class_2246.field_10470,
		class_2246.field_10397
	);
	protected static final Map<class_2248, class_2248> field_7898 = new Builder<class_2248, class_2248>()
		.put(class_2246.field_10126, class_2246.field_10250)
		.put(class_2246.field_10431, class_2246.field_10519)
		.put(class_2246.field_10178, class_2246.field_10374)
		.put(class_2246.field_10010, class_2246.field_10244)
		.put(class_2246.field_9999, class_2246.field_10103)
		.put(class_2246.field_10533, class_2246.field_10622)
		.put(class_2246.field_10307, class_2246.field_10204)
		.put(class_2246.field_10511, class_2246.field_10366)
		.put(class_2246.field_10303, class_2246.field_10084)
		.put(class_2246.field_10306, class_2246.field_10254)
		.put(class_2246.field_10155, class_2246.field_10558)
		.put(class_2246.field_10037, class_2246.field_10436)
		.build();

	protected class_1743(class_1832 arg, class_1792.class_1793 arg2) {
		super(arg, field_7899, arg2);
	}

	@Override
	public float method_7865(class_1799 arg, class_2680 arg2) {
		class_3614 lv = arg2.method_11620();
		return lv != class_3614.field_15932 && lv != class_3614.field_15935 && lv != class_3614.field_15956 && lv != class_3614.field_15946
			? super.method_7865(arg, arg2)
			: this.field_7940;
	}

	@Override
	protected class_5117 method_26739() {
		return class_5117.field_23646;
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_2680 lv3 = lv.method_8320(lv2);
		class_2248 lv4 = (class_2248)field_7898.get(lv3.method_11614());
		if (lv4 != null) {
			class_1657 lv5 = arg.method_8036();
			lv.method_8396(lv5, lv2, class_3417.field_14675, class_3419.field_15245, 1.0F, 1.0F);
			if (!lv.field_9236) {
				lv.method_8652(lv2, lv4.method_9564().method_11657(class_2465.field_11459, lv3.method_11654(class_2465.field_11459)), 11);
				if (lv5 != null) {
					arg.method_8041().method_7956(1, lv5, arg2 -> arg2.method_20236(arg.method_20287()));
				}
			}

			return class_1269.field_5812;
		} else {
			return class_1269.field_5811;
		}
	}
}
