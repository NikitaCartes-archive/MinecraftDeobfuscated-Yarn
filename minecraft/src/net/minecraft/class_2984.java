package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_2984 implements class_3037 {
	public final int field_13385;

	public class_2984(int i) {
		this.field_13385 = i;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.field_13385))));
	}

	public static <T> class_2984 method_12871(Dynamic<T> dynamic) {
		int i = dynamic.get("count").asInt(0);
		return new class_2984(i);
	}
}
