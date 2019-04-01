package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3299 implements class_2998 {
	public final int field_14290;

	public class_3299(int i) {
		this.field_14290 = i;
	}

	@Override
	public <T> Dynamic<T> method_16585(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("chance"), dynamicOps.createInt(this.field_14290))));
	}

	public static class_3299 method_14485(Dynamic<?> dynamic) {
		int i = dynamic.get("chance").asInt(0);
		return new class_3299(i);
	}
}
