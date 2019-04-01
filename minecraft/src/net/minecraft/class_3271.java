package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3271 implements class_2998 {
	public final int field_14200;
	public final float field_14199;

	public class_3271(int i, float f) {
		this.field_14200 = i;
		this.field_14199 = f;
	}

	@Override
	public <T> Dynamic<T> method_16585(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("count"), dynamicOps.createInt(this.field_14200), dynamicOps.createString("chance"), dynamicOps.createFloat(this.field_14199)
				)
			)
		);
	}

	public static class_3271 method_14422(Dynamic<?> dynamic) {
		int i = dynamic.get("count").asInt(0);
		float f = dynamic.get("chance").asFloat(0.0F);
		return new class_3271(i, f);
	}
}
