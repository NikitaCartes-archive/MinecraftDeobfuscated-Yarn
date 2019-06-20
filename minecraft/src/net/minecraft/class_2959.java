package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_2959 implements class_3037 {
	public final float field_13352;

	public class_2959(float f) {
		this.field_13352 = f;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("probability"), dynamicOps.createFloat(this.field_13352))));
	}

	public static <T> class_2959 method_12828(Dynamic<T> dynamic) {
		float f = dynamic.get("probability").asFloat(0.0F);
		return new class_2959(f);
	}
}
