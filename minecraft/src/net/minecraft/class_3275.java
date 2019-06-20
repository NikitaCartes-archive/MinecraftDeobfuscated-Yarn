package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3275 implements class_2998 {
	public final int field_14208;
	public final double field_14206;
	public final double field_14205;
	public final class_2902.class_2903 field_14207;

	public class_3275(int i, double d, double e, class_2902.class_2903 arg) {
		this.field_14208 = i;
		this.field_14206 = d;
		this.field_14205 = e;
		this.field_14207 = arg;
	}

	@Override
	public <T> Dynamic<T> method_16585(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("noise_to_count_ratio"),
					dynamicOps.createInt(this.field_14208),
					dynamicOps.createString("noise_factor"),
					dynamicOps.createDouble(this.field_14206),
					dynamicOps.createString("noise_offset"),
					dynamicOps.createDouble(this.field_14205),
					dynamicOps.createString("heightmap"),
					dynamicOps.createString(this.field_14207.method_12605())
				)
			)
		);
	}

	public static class_3275 method_14427(Dynamic<?> dynamic) {
		int i = dynamic.get("noise_to_count_ratio").asInt(10);
		double d = dynamic.get("noise_factor").asDouble(80.0);
		double e = dynamic.get("noise_offset").asDouble(0.0);
		class_2902.class_2903 lv = class_2902.class_2903.method_12609(dynamic.get("heightmap").asString("OCEAN_FLOOR_WG"));
		return new class_3275(i, d, e, lv);
	}
}
