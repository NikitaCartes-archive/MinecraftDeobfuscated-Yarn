package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class class_3114 implements class_3037 {
	public final class_3411.class_3413 field_13709;
	public final float field_13708;
	public final float field_13707;

	public class_3114(class_3411.class_3413 arg, float f, float g) {
		this.field_13709 = arg;
		this.field_13708 = f;
		this.field_13707 = g;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("biome_temp"),
					dynamicOps.createString(this.field_13709.method_14831()),
					dynamicOps.createString("large_probability"),
					dynamicOps.createFloat(this.field_13708),
					dynamicOps.createString("cluster_probability"),
					dynamicOps.createFloat(this.field_13707)
				)
			)
		);
	}

	public static <T> class_3114 method_13573(Dynamic<T> dynamic) {
		class_3411.class_3413 lv = class_3411.class_3413.method_14830(dynamic.get("biome_temp").asString(""));
		float f = dynamic.get("large_probability").asFloat(0.0F);
		float g = dynamic.get("cluster_probability").asFloat(0.0F);
		return new class_3114(lv, f, g);
	}
}
