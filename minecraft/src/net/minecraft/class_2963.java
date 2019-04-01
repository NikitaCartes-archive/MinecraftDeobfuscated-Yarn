package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_2963 implements class_3037 {
	public final class_2680 field_13356;

	public class_2963(class_2680 arg) {
		this.field_13356 = arg;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), class_2680.method_16550(dynamicOps, this.field_13356).getValue()))
		);
	}

	public static <T> class_2963 method_12842(Dynamic<T> dynamic) {
		class_2680 lv = (class_2680)dynamic.get("state").map(class_2680::method_11633).orElse(class_2246.field_10124.method_9564());
		return new class_2963(lv);
	}
}
