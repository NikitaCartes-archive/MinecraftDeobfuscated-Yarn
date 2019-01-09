package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;

public class class_2549 extends class_2190 {
	public static final class_2753 field_11724 = class_2383.field_11177;
	private static final Map<class_2350, class_265> field_11725 = Maps.newEnumMap(
		ImmutableMap.of(
			class_2350.field_11043,
			class_2248.method_9541(4.0, 4.0, 8.0, 12.0, 12.0, 16.0),
			class_2350.field_11035,
			class_2248.method_9541(4.0, 4.0, 0.0, 12.0, 12.0, 8.0),
			class_2350.field_11034,
			class_2248.method_9541(0.0, 4.0, 4.0, 8.0, 12.0, 12.0),
			class_2350.field_11039,
			class_2248.method_9541(8.0, 4.0, 4.0, 16.0, 12.0, 12.0)
		)
	);

	protected class_2549(class_2484.class_2485 arg, class_2248.class_2251 arg2) {
		super(arg, arg2);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11724, class_2350.field_11043));
	}

	@Override
	public String method_9539() {
		return this.method_8389().method_7876();
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return (class_265)field_11725.get(arg.method_11654(field_11724));
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = this.method_9564();
		class_1922 lv2 = arg.method_8045();
		class_2338 lv3 = arg.method_8037();
		class_2350[] lvs = arg.method_7718();

		for (class_2350 lv4 : lvs) {
			if (lv4.method_10166().method_10179()) {
				class_2350 lv5 = lv4.method_10153();
				lv = lv.method_11657(field_11724, lv5);
				if (!lv2.method_8320(lv3.method_10093(lv4)).method_11587(arg)) {
					return lv;
				}
			}
		}

		return null;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_11724, arg2.method_10503(arg.method_11654(field_11724)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_11724)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11724);
	}
}
