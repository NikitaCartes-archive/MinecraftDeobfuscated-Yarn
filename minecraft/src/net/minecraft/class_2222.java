package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;

public class class_2222 extends class_2221 {
	public static final class_2753 field_9933 = class_2383.field_11177;
	private static final Map<class_2350, class_265> field_9934 = Maps.newEnumMap(
		ImmutableMap.of(
			class_2350.field_11043,
			class_2248.method_9541(0.0, 4.0, 5.0, 16.0, 12.0, 16.0),
			class_2350.field_11035,
			class_2248.method_9541(0.0, 4.0, 0.0, 16.0, 12.0, 11.0),
			class_2350.field_11039,
			class_2248.method_9541(5.0, 4.0, 0.0, 16.0, 12.0, 16.0),
			class_2350.field_11034,
			class_2248.method_9541(0.0, 4.0, 0.0, 11.0, 12.0, 16.0)
		)
	);

	protected class_2222(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_9933, class_2350.field_11043).method_11657(field_9940, Boolean.valueOf(true)));
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return (class_265)field_9934.get(arg.method_11654(field_9933));
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_9933, arg2.method_10503(arg.method_11654(field_9933)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_9933)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_9933, field_9940);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_9940)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return arg2.method_10153() == arg.method_11654(field_9933) && !arg.method_11591(arg4, arg5) ? class_2246.field_10124.method_9564() : arg;
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2350 lv = arg.method_11654(field_9933);
		class_2338 lv2 = arg3.method_10093(lv.method_10153());
		class_2680 lv3 = arg2.method_8320(lv2);
		return class_2248.method_9501(lv3.method_11628(arg2, lv2), lv) && !method_9581(lv3.method_11614());
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = super.method_9605(arg);
		class_1941 lv2 = arg.method_8045();
		class_2338 lv3 = arg.method_8037();
		class_2350[] lvs = arg.method_7718();

		for (class_2350 lv4 : lvs) {
			if (lv4.method_10166().method_10179()) {
				lv = lv.method_11657(field_9933, lv4.method_10153());
				if (lv.method_11591(lv2, lv3)) {
					return lv;
				}
			}
		}

		return null;
	}
}
