package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3137 implements class_3037 {
	public final class_2975<?> field_13740;
	public final class_2975<?> field_13739;

	public class_3137(class_2975<?> arg, class_2975<?> arg2) {
		this.field_13740 = arg;
		this.field_13739 = arg2;
	}

	public class_3137(class_3031<?> arg, class_3037 arg2, class_3031<?> arg3, class_3037 arg4) {
		this(method_13688(arg, arg2), method_13688(arg3, arg4));
	}

	private static <FC extends class_3037> class_2975<FC> method_13688(class_3031<FC> arg, class_3037 arg2) {
		return new class_2975<>(arg, (FC)arg2);
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("feature_true"),
					this.field_13740.method_16584(dynamicOps).getValue(),
					dynamicOps.createString("feature_false"),
					this.field_13739.method_16584(dynamicOps).getValue()
				)
			)
		);
	}

	public static <T> class_3137 method_13687(Dynamic<T> dynamic) {
		class_2975<?> lv = class_2975.method_12861(dynamic.get("feature_true").orElseEmptyMap());
		class_2975<?> lv2 = class_2975.method_12861(dynamic.get("feature_false").orElseEmptyMap());
		return new class_3137(lv, lv2);
	}
}
