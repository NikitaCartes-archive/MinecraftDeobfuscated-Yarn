package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3277 implements class_2998 {
	public final int field_14214;
	public final int field_14213;
	public final int field_14212;

	public class_3277(int i, int j, int k) {
		this.field_14214 = i;
		this.field_14213 = j;
		this.field_14212 = k;
	}

	@Override
	public <T> Dynamic<T> method_16585(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("count"),
					dynamicOps.createInt(this.field_14214),
					dynamicOps.createString("baseline"),
					dynamicOps.createInt(this.field_14213),
					dynamicOps.createString("spread"),
					dynamicOps.createInt(this.field_14212)
				)
			)
		);
	}

	public static class_3277 method_14429(Dynamic<?> dynamic) {
		int i = dynamic.get("count").asInt(0);
		int j = dynamic.get("baseline").asInt(0);
		int k = dynamic.get("spread").asInt(0);
		return new class_3277(i, j, k);
	}
}
