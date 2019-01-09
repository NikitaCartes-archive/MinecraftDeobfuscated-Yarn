package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_2990 implements class_2998 {
	public final float field_13407;
	public final int field_13410;
	public final int field_13409;
	public final int field_13408;

	public class_2990(float f, int i, int j, int k) {
		this.field_13407 = f;
		this.field_13410 = i;
		this.field_13409 = j;
		this.field_13408 = k;
	}

	@Override
	public <T> Dynamic<T> method_16585(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("chance"),
					dynamicOps.createFloat(this.field_13407),
					dynamicOps.createString("bottom_offset"),
					dynamicOps.createInt(this.field_13410),
					dynamicOps.createString("top_offset"),
					dynamicOps.createInt(this.field_13409),
					dynamicOps.createString("top"),
					dynamicOps.createInt(this.field_13408)
				)
			)
		);
	}

	public static class_2990 method_12898(Dynamic<?> dynamic) {
		float f = dynamic.get("chance").asFloat(0.0F);
		int i = dynamic.get("bottom_offset").asInt(0);
		int j = dynamic.get("top_offset").asInt(0);
		int k = dynamic.get("top").asInt(0);
		return new class_2990(f, i, j, k);
	}
}
