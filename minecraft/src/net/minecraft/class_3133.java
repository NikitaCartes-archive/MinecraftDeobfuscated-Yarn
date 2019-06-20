package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3133 implements class_2920, class_3037 {
	public final float field_13738;

	public class_3133(float f) {
		this.field_13738 = f;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createFloat(this.field_13738))));
	}

	public static <T> class_3133 method_13674(Dynamic<T> dynamic) {
		float f = dynamic.get("probability").asFloat(0.0F);
		return new class_3133(f);
	}
}
