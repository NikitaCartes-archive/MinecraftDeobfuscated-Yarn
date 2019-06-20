package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3034 implements class_3037 {
	public final int field_13601;

	public class_3034(int i) {
		this.field_13601 = i;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("radius"), dynamicOps.createInt(this.field_13601))));
	}

	public static <T> class_3034 method_13164(Dynamic<T> dynamic) {
		int i = dynamic.get("radius").asInt(0);
		return new class_3034(i);
	}
}
