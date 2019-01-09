package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3187 implements class_3037 {
	public final class_3610 field_13850;

	public class_3187(class_3610 arg) {
		this.field_13850 = arg;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("state"), class_3610.method_16458(dynamicOps, this.field_13850).getValue()))
		);
	}

	public static <T> class_3187 method_13984(Dynamic<T> dynamic) {
		class_3610 lv = (class_3610)dynamic.get("state").map(class_3610::method_15765).orElse(class_3612.field_15906.method_15785());
		return new class_3187(lv);
	}
}
