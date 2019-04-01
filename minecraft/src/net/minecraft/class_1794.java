package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;

public class class_1794 extends class_1831 {
	private final float field_8022;
	protected static final Map<class_2248, class_2680> field_8023 = Maps.<class_2248, class_2680>newHashMap(
		ImmutableMap.of(
			class_2246.field_10219,
			class_2246.field_10362.method_9564(),
			class_2246.field_10194,
			class_2246.field_10362.method_9564(),
			class_2246.field_10566,
			class_2246.field_10362.method_9564(),
			class_2246.field_10253,
			class_2246.field_10566.method_9564()
		)
	);

	public class_1794(class_1832 arg, float f, class_1792.class_1793 arg2) {
		super(arg, arg2);
		this.field_8022 = f;
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		if (arg.method_8038() != class_2350.field_11033 && lv.method_8320(lv2.method_10084()).method_11588()) {
			class_2680 lv3 = (class_2680)field_8023.get(lv.method_8320(lv2).method_11614());
			if (lv3 != null) {
				class_1657 lv4 = arg.method_8036();
				lv.method_8396(lv4, lv2, class_3417.field_14846, class_3419.field_15245, 1.0F, 1.0F);
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

	@Override
	public boolean method_7873(class_1799 arg, class_1309 arg2, class_1309 arg3) {
		arg.method_7956(1, arg3);
		return true;
	}

	@Override
	public Multimap<String, class_1322> method_7844(class_1304 arg) {
		Multimap<String, class_1322> multimap = super.method_7844(arg);
		if (arg == class_1304.field_6173) {
			multimap.put(class_1612.field_7363.method_6167(), new class_1322(field_8006, "Weapon modifier", 0.0, class_1322.class_1323.field_6328));
			multimap.put(class_1612.field_7356.method_6167(), new class_1322(field_8001, "Weapon modifier", (double)this.field_8022, class_1322.class_1323.field_6328));
		}

		return multimap;
	}
}
