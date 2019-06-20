package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3273 implements class_2998 {
	public final int field_14204;

	public class_3273(int i) {
		this.field_14204 = i;
	}

	@Override
	public <T> Dynamic<T> method_16585(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.field_14204))));
	}

	public static class_3273 method_14425(Dynamic<?> dynamic) {
		int i = dynamic.get("count").asInt(0);
		return new class_3273(i);
	}
}
