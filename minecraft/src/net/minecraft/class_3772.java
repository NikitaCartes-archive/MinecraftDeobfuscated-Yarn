package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3772 implements class_3037 {
	public final double field_16657;

	public class_3772(double d) {
		this.field_16657 = d;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createDouble(this.field_16657))));
	}

	public static <T> class_3772 method_16589(Dynamic<T> dynamic) {
		float f = dynamic.get("probability").asFloat(0.0F);
		return new class_3772((double)f);
	}
}
