package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3276 implements class_2998 {
	public final int field_14211;
	public final float field_14209;
	public final int field_14210;

	public class_3276(int i, float f, int j) {
		this.field_14211 = i;
		this.field_14209 = f;
		this.field_14210 = j;
	}

	@Override
	public <T> Dynamic<T> method_16585(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("count"),
					dynamicOps.createInt(this.field_14211),
					dynamicOps.createString("extra_chance"),
					dynamicOps.createFloat(this.field_14209),
					dynamicOps.createString("extra_count"),
					dynamicOps.createInt(this.field_14210)
				)
			)
		);
	}

	public static class_3276 method_14428(Dynamic<?> dynamic) {
		int i = dynamic.get("count").asInt(0);
		float f = dynamic.get("extra_chance").asFloat(0.0F);
		int j = dynamic.get("extra_count").asInt(0);
		return new class_3276(i, f, j);
	}
}
