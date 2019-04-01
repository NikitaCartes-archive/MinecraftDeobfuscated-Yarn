package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3163 implements class_3037 {
	public final int field_13789;
	public final double field_13788;

	public class_3163(int i, double d) {
		this.field_13789 = i;
		this.field_13788 = d;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("count"),
					dynamicOps.createInt(this.field_13789),
					dynamicOps.createString("tall_seagrass_probability"),
					dynamicOps.createDouble(this.field_13788)
				)
			)
		);
	}

	public static <T> class_3163 method_13884(Dynamic<T> dynamic) {
		int i = dynamic.get("count").asInt(0);
		double d = dynamic.get("tall_seagrass_probability").asDouble(0.0);
		return new class_3163(i, d);
	}
}
