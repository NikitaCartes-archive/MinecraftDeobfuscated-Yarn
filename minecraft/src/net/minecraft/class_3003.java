package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3003 implements class_2998 {
	public final double field_13444;
	public final int field_13446;
	public final int field_13445;

	public class_3003(double d, int i, int j) {
		this.field_13444 = d;
		this.field_13446 = i;
		this.field_13445 = j;
	}

	@Override
	public <T> Dynamic<T> method_16585(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("noise_level"),
					dynamicOps.createDouble(this.field_13444),
					dynamicOps.createString("below_noise"),
					dynamicOps.createInt(this.field_13446),
					dynamicOps.createString("above_noise"),
					dynamicOps.createInt(this.field_13445)
				)
			)
		);
	}

	public static class_3003 method_12967(Dynamic<?> dynamic) {
		double d = dynamic.get("noise_level").asDouble(0.0);
		int i = dynamic.get("below_noise").asInt(0);
		int j = dynamic.get("above_noise").asInt(0);
		return new class_3003(d, i, j);
	}
}
