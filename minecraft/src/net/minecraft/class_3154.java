package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3154 implements class_3037 {
	public final class_2680 field_13765;
	public final class_2680 field_13766;

	public class_3154(class_2680 arg, class_2680 arg2) {
		this.field_13765 = arg;
		this.field_13766 = arg2;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("target"),
					class_2680.method_16550(dynamicOps, this.field_13765).getValue(),
					dynamicOps.createString("state"),
					class_2680.method_16550(dynamicOps, this.field_13766).getValue()
				)
			)
		);
	}

	public static <T> class_3154 method_13822(Dynamic<T> dynamic) {
		class_2680 lv = (class_2680)dynamic.get("target").map(class_2680::method_11633).orElse(class_2246.field_10124.method_9564());
		class_2680 lv2 = (class_2680)dynamic.get("state").map(class_2680::method_11633).orElse(class_2246.field_10124.method_9564());
		return new class_3154(lv, lv2);
	}
}
