package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3278 implements class_2998 {
	public final int field_14216;
	public final int field_14215;

	public class_3278(int i, int j) {
		this.field_14216 = i;
		this.field_14215 = j;
	}

	@Override
	public <T> Dynamic<T> method_16585(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("min"), dynamicOps.createInt(this.field_14216), dynamicOps.createString("max"), dynamicOps.createInt(this.field_14215)
				)
			)
		);
	}

	public static class_3278 method_14430(Dynamic<?> dynamic) {
		int i = dynamic.get("min").asInt(0);
		int j = dynamic.get("max").asInt(0);
		return new class_3278(i, j);
	}
}
